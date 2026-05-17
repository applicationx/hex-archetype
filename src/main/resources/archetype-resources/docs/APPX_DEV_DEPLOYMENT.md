#set($d = '$')
# AppX Dev Deployment

This generated project follows the same dev delivery contract as `spring-gateway-base` and `appx-web`.

**Delivery Contract**

On a push to `main`, `.github/workflows/deploy.yml`:

1. Runs on the repository-scoped ARC runner named `arc-${artifactId}`.
2. Builds and tests with Maven and Java `${javaVersion}`.
3. Packages the executable Spring Boot jar from `adapters/inbound-rest` by default.
4. Builds the root `Dockerfile` with in-cluster BuildKit at `buildkitd.buildkit.svc.cluster.local:1234`.
5. Pushes `docker.appx-labs.com/docker/${artifactId}:${d}{GITHUB_SHA}` and `docker.appx-labs.com/docker/${artifactId}:main`.
6. Updates `helm/${artifactId}/values.yaml` with the immutable commit SHA.
7. Commits that Helm value change back to `main` with `[skip ci]`.

Argo CD should watch this repository and deploy from the shared AppX Helm chart plus `helm/${artifactId}/values.yaml`.

**Required GitHub Secrets**

Configure these repository or organization secrets before the first deployment:

- `DOCKER_APPX_LABS_USERNAME`
- `DOCKER_APPX_LABS_PASSWORD`

The workflow also expects the ARC runner pod to mount the BuildKit client certificate secret at `/buildkit-certs`, matching the existing AppX runner values.

**k3s-dev Runner Values**

Create a file like this in `~/github/k3s-dev/manifests/github-actions-runner/${artifactId}-runner-values.yaml`:

```yaml
githubConfigUrl: "https://github.com/applicationx/${artifactId}"
githubConfigSecret: github-pat

maxRunners: 2
minRunners: 0

template:
  spec:
    containers:
      - name: runner
        image: ghcr.io/actions/actions-runner:latest
        command:
          - /home/runner/run.sh
        resources:
          requests:
            cpu: 500m
            memory: 1Gi
          limits:
            memory: 2Gi
        volumeMounts:
          - name: buildkit-client-certs
            mountPath: /buildkit-certs
            readOnly: true
    volumes:
      - name: buildkit-client-certs
        secret:
          secretName: buildkit-client-certs
```

Install that runner scale set using the same chart and release pattern used by the existing AppX runner manifests.

**k3s-dev Argo CD Project**

Create `~/github/k3s-dev/manifests/argocd/${artifactId}-project.yaml`:

```yaml
apiVersion: argoproj.io/v1alpha1
kind: AppProject
metadata:
  name: ${artifactId}
  namespace: argocd
  finalizers:
    - resources-finalizer.argocd.argoproj.io
spec:
  description: ${artifactId} delivery
  sourceRepos:
    - https://github.com/applicationx/${artifactId}.git
    - https://github.com/applicationx/helm-charts.git
  destinations:
    - server: https://kubernetes.default.svc
      namespace: gateway-system
```

**k3s-dev Argo CD Application**

Create `~/github/k3s-dev/manifests/argocd/${artifactId}-application.yaml`:

```yaml
apiVersion: argoproj.io/v1alpha1
kind: Application
metadata:
  name: ${artifactId}
  namespace: argocd
  finalizers:
    - resources-finalizer.argocd.argoproj.io
spec:
  project: ${artifactId}
  sources:
    - repoURL: https://github.com/applicationx/helm-charts.git
      targetRevision: 157559f15b05f94ae2233b1441d5bb22f75481bd
      path: charts/appx-spring-boot
      helm:
        releaseName: ${artifactId}
        valueFiles:
          - ${d}values/helm/${artifactId}/values.yaml
    - repoURL: https://github.com/applicationx/${artifactId}.git
      targetRevision: main
      ref: values
  destination:
    server: https://kubernetes.default.svc
    namespace: gateway-system
  syncPolicy:
    automated:
      prune: true
      selfHeal: true
    syncOptions:
      - CreateNamespace=true
```

Add both files to the `k3s-dev` Argo CD `kustomization.yaml`.

**Public Route**

The generated `helm/${artifactId}/values.yaml` disables the `HTTPRoute` by default. Enable it only after choosing a real hostname and Gateway listener:

```yaml
httproute:
  enabled: true
  hostnames:
    - ${artifactId}.appx-cloud.com
  parentRefs:
    - name: shared-gateway
      namespace: networking
      sectionName: https
```

For services meant to sit behind `spring-gateway-base`, leave public routing disabled and add the service route in the gateway instead.

**Verification**

After pushing to `main`:

```bash
gh run list --workflow deploy.yml --limit 5
kubectl get application ${artifactId} -n argocd
kubectl get pods -n gateway-system -l app.kubernetes.io/instance=${artifactId}
```

The expected flow is: GitHub Actions succeeds, a `[skip ci]` image promotion commit lands on `main`, Argo CD reports `Synced` and `Healthy`, and the Kubernetes deployment uses the promoted commit SHA image tag.
