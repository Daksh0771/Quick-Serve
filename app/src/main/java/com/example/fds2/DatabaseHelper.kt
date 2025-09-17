package com.example.fds2.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "FoodApp.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(
            "CREATE TABLE users (" +
                    "user_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "username TEXT, " +
                    "mobile TEXT UNIQUE, " +
                    "email TEXT UNIQUE, " +
                    "is_logged_in INTEGER DEFAULT 0)"
        )

        db?.execSQL(
            "CREATE TABLE menu(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT, " +
                    "price INTEGER)"
        )

        db?.execSQL(
            "CREATE TABLE restaurants(" +
                    "rest_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "rest_name TEXT, " +
                    "rest_category TEXT, " +
                    "rest_rating REAL," +
                    "is_active INTEGER DEFAULT 1)"
        )

        db?.execSQL(
            "CREATE TABLE cart(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "itemName TEXT, " +
                    "price INTEGER)"
        )

        db?.execSQL(
            "CREATE TABLE orders (" +
                    "order_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "user_id INTEGER NOT NULL, " +
                    "customer_name TEXT, " +
                    "address TEXT, " +
                    "phone TEXT, " +
                    "date TEXT, " +
                    "FOREIGN KEY(user_id) REFERENCES users(user_id) ON DELETE CASCADE)"
        )

        db?.execSQL(
            "CREATE TABLE order_items (" +
                    "item_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "order_id INTEGER, " +
                    "item_name TEXT, " +
                    "quantity INTEGER, " +
                    "price INTEGER, " +
                    "FOREIGN KEY(order_id) REFERENCES orders(order_id) ON DELETE CASCADE)"
        )
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

            db?.execSQL("DROP TABLE IF EXISTS users")
            db?.execSQL("DROP TABLE IF EXISTS menu")
            db?.execSQL("DROP TABLE IF EXISTS cart")
            db?.execSQL("DROP TABLE IF EXISTS orders")
            db?.execSQL("DROP TABLE IF EXISTS order_items")
            onCreate(db)
        }

        fun insertUser(username: String, mobile: String, email: String): Long {
            val db = writableDatabase
            val values = ContentValues().apply {
                put("username", username)
                put("mobile", mobile)
                put("email", email)
            }
            val result =
                db.insertWithOnConflict("users", null, values, SQLiteDatabase.CONFLICT_IGNORE)
            db.close()
            return result
        }

        fun isUserExists(): Boolean {
            val db = readableDatabase
            val cursor = db.rawQuery("SELECT * FROM users LIMIT 1", null)
            val exists = cursor.moveToFirst()
            cursor.close()
            db.close()
            return exists
        }

        fun checkUser(username: String, mobile: String): Boolean {
            val db = readableDatabase
            val cursor = db.rawQuery(
                "SELECT * FROM users WHERE username=? AND mobile=?",
                arrayOf(username, mobile)
            )
            val exists = cursor.count > 0
            cursor.close()
            db.close()
            return exists
        }

    fun loginUser(username: String, mobile: String) {
        val db = writableDatabase

        // Log out all users first
        db.execSQL("UPDATE users SET is_logged_in = 0")

        // Log in the selected user
        db.execSQL(
            "UPDATE users SET is_logged_in = 1 WHERE username = ? AND mobile = ?",
            arrayOf(username, mobile)
        )

        db.close()
    }

    fun logoutUser() {
        val db = writableDatabase
        db.execSQL("UPDATE users SET is_logged_in = 0")
        db.close()
    }

    fun getLoggedInUsername(): Pair<String, String>? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT username, email FROM users WHERE is_logged_in = 1 LIMIT 1",
            null
        )

        var result: Pair<String, String>? = null
        if (cursor.moveToFirst()) {
            val username = cursor.getString(cursor.getColumnIndexOrThrow("username"))
            val email = cursor.getString(cursor.getColumnIndexOrThrow("email"))
            result = Pair(username, email)
        }

        cursor.close()
        db.close()
        return result
    }

    // In your DatabaseHelper class, add this function:
    fun getLoggedInUser(): User? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT user_id, username, mobile, email FROM users WHERE is_logged_in = 1 LIMIT 1",
            null
        )

        var user: User? = null
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"))
            val name = cursor.getString(cursor.getColumnIndexOrThrow("username"))
            val phone = cursor.getString(cursor.getColumnIndexOrThrow("mobile"))
            val email = cursor.getString(cursor.getColumnIndexOrThrow("email"))
            user = User(id, name, phone, email)
        }
        cursor.close()
        db.close()
        return user
    }

    data class User(
        val id: Int,
        val name: String,
        val phone: String,
        val email: String
    )


    fun insertOrder(
        userId: Int,
        customerName: String,
        address: String,
        phone: String,
        cartItems: Map<String, Pair<Int, Int>>
    ): Long {
        val db = writableDatabase
        db.beginTransaction()

        val orderValues = ContentValues().apply {
            put("user_id", userId) // <- Add this
            put("customer_name", customerName)
            put("address", address)
            put("phone", phone)
            put("date", System.currentTimeMillis().toString())
        }

        val orderId = db.insert("orders", null, orderValues)

    if (orderId != -1L) {
            cartItems.forEach { (itemName, pair) ->
                val (quantity, price) = pair
                val itemValues = ContentValues().apply {
                    put("order_id", orderId)
                    put("item_name", itemName)
                    put("quantity", quantity)
                    put("price", price)
                }
                db.insert("order_items", null, itemValues)
            }

            db.setTransactionSuccessful()
        }

        db.endTransaction()
        db.close()

        return orderId
    }

    data class Order(
        val orderId: Long,
        val customerName: String,
        val address: String,
        val phone: String,
        val date: String
    )
    fun getAllOrders(userId: Int): List<Order> {
        val db = readableDatabase
        val orders = mutableListOf<Order>()

        val cursor = db.rawQuery(
            "SELECT * FROM orders WHERE user_id = ? ORDER BY order_id ASC",
            arrayOf(userId.toString())
        )

        while (cursor.moveToNext()) {
            orders.add(
                Order(
                    orderId = cursor.getLong(cursor.getColumnIndexOrThrow("order_id")),
                    customerName = cursor.getString(cursor.getColumnIndexOrThrow("customer_name")),
                    address = cursor.getString(cursor.getColumnIndexOrThrow("address")),
                    phone = cursor.getString(cursor.getColumnIndexOrThrow("phone")),
                    date = cursor.getString(cursor.getColumnIndexOrThrow("date"))
                )
            )
        }

        cursor.close()
        db.close()
        return orders
    }


    data class OrderItem(
        val itemName: String,
        val quantity: Int,
        val price: Int
    )

    fun getOrderDetails(orderId: Long): Pair<Order, List<OrderItem>>? {
        val db = readableDatabase

        val orderCursor = db.rawQuery("SELECT * FROM orders WHERE order_id = ?", arrayOf(orderId.toString()))
        if (!orderCursor.moveToFirst()) {
            orderCursor.close()
            db.close()
            return null
        }

        val order = Order(
            orderId = orderCursor.getLong(orderCursor.getColumnIndexOrThrow("order_id")),
            customerName = orderCursor.getString(orderCursor.getColumnIndexOrThrow("customer_name")),
            address = orderCursor.getString(orderCursor.getColumnIndexOrThrow("address")),
            phone = orderCursor.getString(orderCursor.getColumnIndexOrThrow("phone")),
            date = orderCursor.getString(orderCursor.getColumnIndexOrThrow("date"))
        )

        orderCursor.close()

        val itemCursor = db.rawQuery("SELECT * FROM order_items WHERE order_id = ?", arrayOf(orderId.toString()))
        val items = mutableListOf<OrderItem>()
        while (itemCursor.moveToNext()) {
            items.add(
                OrderItem(
                    itemName = itemCursor.getString(itemCursor.getColumnIndexOrThrow("item_name")),
                    quantity = itemCursor.getInt(itemCursor.getColumnIndexOrThrow("quantity")),
                    price = itemCursor.getInt(itemCursor.getColumnIndexOrThrow("price"))
                )
            )
        }

        itemCursor.close()
        db.close()

        return Pair(order, items)
    }

    fun deleteUserAndAllData(userId: Int, username: String, mobile: String): Boolean {
        val db = writableDatabase
        db.beginTransaction()
        return try {
            // Step 1: Find all orders by customer_name and phone
            val orderIds = mutableListOf<Long>()
            val cursor = db.rawQuery(
                "SELECT order_id FROM orders WHERE customer_name = ? AND phone = ?",
                arrayOf(username, mobile)
            )
            while (cursor.moveToNext()) {
                val orderId = cursor.getLong(cursor.getColumnIndexOrThrow("order_id"))
                orderIds.add(orderId)
            }
            cursor.close()

            // Step 2: Delete all order_items for those orders
            for (orderId in orderIds) {
                db.delete("order_items", "order_id = ?", arrayOf(orderId.toString()))
            }

            // Step 3: Delete orders for the user
            db.delete("orders", "customer_name = ? AND phone = ?", arrayOf(username, mobile))

            // Step 4: Delete the user
            val deletedRows = db.delete("users", "user_id = ?", arrayOf(userId.toString()))

            db.setTransactionSuccessful()
            deletedRows > 0
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            db.endTransaction()
            db.close()
        }
    }

    fun insertRestaurant(rest_name: String, rest_category: String, rest_rating: Double): Long {
            val db = writableDatabase
            val values = ContentValues()
            values.put("name", rest_name)
            values.put("category", rest_category)
            values.put("rating", rest_rating)
            val reslist = db.insert("restaurants", null, values)
            db.close()
            return reslist
        }

    fun getAllRestaurants(): List<Restaurant> {
        val resList = mutableListOf<Restaurant>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM restaurants", null)
        if (cursor.moveToFirst()) {
            do {
                val res = Restaurant(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("rest_id")),
                    name = cursor.getString(cursor.getColumnIndexOrThrow("rest_name")),
                    category = cursor.getString(cursor.getColumnIndexOrThrow("rest_category")),
                    rating = cursor.getDouble(cursor.getColumnIndexOrThrow("rest_rating")),
                    isActive = cursor.getInt(cursor.getColumnIndexOrThrow("is_active")) == 1
                )
                resList.add(res)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return resList
    }
    fun updateRestaurantActiveState(restaurantId: Int, isActive: Boolean) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("is_active", if (isActive) 1 else 0)
        }
        db.update("restaurants", values, "rest_id = ?", arrayOf(restaurantId.toString()))
        db.close()
    }

    data class Restaurant(
        val id: Int,
        val name: String,
        val category: String,
        val rating: Double,
        var isActive: Boolean
    )

}
