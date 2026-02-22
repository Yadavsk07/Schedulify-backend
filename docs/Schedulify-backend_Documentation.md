# Schedulify-backend Documentation

## Overview
Schedulify-backend is a powerful and efficient backend system designed to facilitate scheduling and management of various tasks and events. It provides a robust API for integration with frontend applications and includes features for user management, event tracking, and notifications.

## Features
- **User Authentication**: Secure user registration and login process.
- **Event Management**: Create, update, and delete events.
- **Notifications**: Real-time notifications for scheduled events.
- **Reporting**: Generate reports on user activities and event occurrences.

## Database Schema
- **Users Table**: Stores user information (id, name, email, password_hash).  
- **Events Table**: Stores event details (id, user_id, title, start_time, end_time).
- **Notifications Table**: Stores notification details (id, user_id, event_id, message, status).

### Relationships
- Users can have multiple events.
- Events can have multiple notifications associated with them.

## Business Logic
The backend processes include user authentication, event creation and modification requests, and the sending of notifications based on event schedules. Each operation follows strict validation to ensure data integrity and security.

## API Endpoints
- **POST /api/users**  - Register a new user.  
  *Parameters*: {"name": string, "email": string, "password": string}  
  *Response*: 201 Created or 400 Bad Request

- **POST /api/events**  - Create a new event.  
  *Parameters*: {"user_id": integer, "title": string, "start_time": string, "end_time": string}  
  *Response*: 201 Created or 400 Bad Request

- **GET /api/events/{id}**  - Retrieve event details by id.  
  *Response*: 200 OK or 404 Not Found

- **DELETE /api/events/{id}**  - Delete an event by id.  
  *Response*: 204 No Content or 404 Not Found

- **POST /api/notifications**  - Create a new notification.  
  *Parameters*: {"user_id": integer, "event_id": integer, "message": string}  
  *Response*: 201 Created or 400 Bad Request

