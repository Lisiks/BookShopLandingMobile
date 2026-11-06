package com.example.bookshopapp.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
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
        val preferences = dataStore.data.first()
        val savedCartData = preferences[cartKey]
        val cartMap: MutableMap<Int, Int> = Json.decodeFromString(savedCartData ?: "{}")
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

        if (itemId in cartMap.keys) {
            cartMap.remove(itemId)
        }

        dataStore.edit { prefs ->
            val jsonString: String = Json.encodeToString(cartMap)
            prefs[cartKey] = jsonString
        }
    }


    suspend fun deleteCartMany(itemIdList: List<Int>) {
        val preferences = dataStore.data.first()
        val savedCartData = preferences[cartKey]
        val cartMap: MutableMap<Int, Int> = Json.decodeFromString(savedCartData ?: "{}")

        itemIdList.forEach { itemId ->
            if (itemId in cartMap.keys) {
                cartMap.remove(itemId)
            }
        }

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