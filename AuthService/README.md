<!-- docker pull prashantknowsit/jwt-learn  
docker run -d -p 8080:8080 --name jwt-app prashantknowsit/jwt-learn  

//Register
POST-> http://localhost:8080/auth/register
{
  "username": "testuser",
  "email": "test@example.com",
  "password": "password123",
  "role": "ROLE_USER"
}



// Login
POST-> http://localhost:8080/auth/login
{
  "username": "testuser",
  "password": "password123"
}



// Verify
GET-> http://localhost:8080
Authorization = Bearer 898fhh4t8nn4n9t94txxxxx -->
