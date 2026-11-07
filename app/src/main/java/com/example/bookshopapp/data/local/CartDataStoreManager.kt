package com.example.bookshopapp.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.bookshopapp.data.CartException
import kotlinx.coroutines.flow.first
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "my_app_prefs")


class CartDataStoreManager(context: Context) {
    private val dataStore = context.applicationContext.dataStore
    private val cartKey = stringPreferencesKey("cart")

    suspend fun getCartData(): Map<Int, Int> {
        val preferences = dataStore.data.first()
        val savedCartData = preferences[cartKey]
        return Json.decodeFromString(savedCartData ?: "{}")
    }

    suspend fun addCartItem(itemId: Int, itemCount: Int) {
        if (itemCount < 1) {
            throw CartException("Item count must be greater than 0")
        }

        val preferences = dataStore.data.first()
        val savedCartData = preferences[cartKey]
        val cartMap: MutableMap<Int, Int> = Json.decodeFromString(savedCartData ?: "{}")

        if (cartMap.size >= 10 && itemId !in cartMap.keys) {
            throw CartException("Max cart items limit is 10")
        }

        cartMap[itemId] = (cartMap[itemId] ?: 0) + itemCount



        dataStore.edit { prefs ->
            val jsonString: String = Json.encodeToString(cartMap)
            prefs[cartKey]  = jsonString

        }
    }


    suspend fun deleteCartItem(itemId: Int) {
        val preferences = dataStore.data.first()
        val savedCartData = preferences[cartKey]
        val cartMap: MutableMap<Int, Int> = Json.decodeFromString(savedCartData ?: "{}")

        cartMap.remove(itemId)

        dataStore.edit { prefs ->
            val jsonString: String = Json.encodeToString(cartMap)
            prefs[cartKey] = jsonString
        }
    }


    suspend fun deleteCartMany(itemIdList: List<Int>) {
        val preferences = dataStore.data.first()
        val savedCartData = preferences[cartKey]
        val cartMap: MutableMap<Int, Int> = Json.decodeFromString(savedCartData ?: "{}")

        cartMap -= itemIdList.toSet()

        dataStore.edit { prefs ->
            val jsonString: String = Json.encodeToString(cartMap)
            prefs[cartKey] = jsonString
        }
    }


    suspend fun deleteCartAll() {
        dataStore.edit { prefs ->
            val jsonString: String = Json.encodeToString(mapOf<Int, Int>())
            prefs[cartKey] = jsonString
        }
    }

}