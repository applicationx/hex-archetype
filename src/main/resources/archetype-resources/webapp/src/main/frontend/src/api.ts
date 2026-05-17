export interface RegisterCustomerRequest {
  email: string
}

export interface RegisterCustomerResponse {
  id: string
}

export async function registerCustomer(
  request: RegisterCustomerRequest
): Promise<RegisterCustomerResponse> {
  const response = await fetch('/api/v1/customers', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(request),
  })

  if (!response.ok) {
    throw new Error(`Registration failed with status ${response.status}`)
  }

  return response.json() as Promise<RegisterCustomerResponse>
}
