# How to Create Admin Accounts

## How Admin Accounts Work

Admin accounts have special permissions:
- **Search Users** - Find users by name, email, account number, or phone
- **Assign Roles** - Change user roles (customer, banker, bank_manager, admin)
- **Manage Loans** - View and approve/reject loan requests
- **Access Admin Dashboard** - Special dashboard at `/dash`

## Creating Your First Admin

Since you need an admin to assign roles, you need to create the first admin manually. Here are two methods:

### Method 1: Direct Database Insert (Easiest)

1. **Open MySQL Workbench**
2. **Connect to your database** (`mybankuml`)
3. **Run this SQL** (replace with your details):

```sql
INSERT INTO users (user_id, user_password, user_name, user_email, user_phone, user_role, is_active)
VALUES ('admin001', 'your_password', 'Admin User', 'admin@example.com', '555-0000', 'admin', TRUE);
```

4. **Login** with:
   - User ID or Email: `admin001` (or `admin@example.com`)
   - Password: `your_password`

### Method 2: Register then Update Database

1. **Register a normal account** through the signup page
2. **Open MySQL Workbench**
3. **Update the user's role**:

```sql
UPDATE users 
SET user_role = 'admin' 
WHERE user_id = 'YOUR_USER_ID';
```

Replace `YOUR_USER_ID` with the User ID you registered with.

## Creating Additional Admins

Once you have one admin account, you can create more through the UI:

1. **Login as an admin** → You'll be redirected to `/dash`
2. **Go to "Search Users"** (in sidebar or `/admin/search`)
3. **Search for the user** you want to make admin
4. **Click "View" or "Assign Role"** on the user
5. **Select "Admin"** from the role dropdown
6. **Click "Assign Role"**

The user will now have admin permissions!

## Admin Features

### Admin Dashboard (`/dash`)
- View pending loan requests
- Quick stats
- Access to all admin features

### Search Users (`/admin/search`)
- Search by name, account number, phone, or user type
- View user details
- Assign roles

### Role Assignment (`/admin/roles`)
- Change any user's role to:
  - **Customer** - Regular banking user
  - **Banker** - Can verify transactions
  - **Bank Manager** - Can approve loans
  - **Admin** - Full system access

## Quick Test

After creating an admin:
1. Logout (if logged in)
2. Login with your admin credentials
3. You should be redirected to `/dash` (admin dashboard)
4. You should see "Loan Requests", "Search Users", etc. in the sidebar

## Troubleshooting

**Can't login as admin?**
- Check the `user_role` in database: `SELECT user_id, user_role FROM users WHERE user_id = 'admin001';`
- Make sure it says `admin` (not `customer`)

**Don't see admin features?**
- Make sure you're logged in as admin
- Check browser console for errors
- Try logging out and back in

**Need to reset admin password?**
```sql
UPDATE users 
SET user_password = 'new_password' 
WHERE user_id = 'admin001';
```

