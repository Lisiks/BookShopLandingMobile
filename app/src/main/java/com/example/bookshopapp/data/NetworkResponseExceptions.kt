package com.example.bookshopapp.data

// Кидаем когда запрос падает из за сети
class InternetException(message: String): Exception(message)


// Кидаем только при входе
class LoginException(message: String): Exception(message)


// Кидаем если чето не найдено
class NotFoundException(message: String): Exception(message)


// Кидаем при ошибке 403 на любое действие, требующее авторизации
class AuthException(message: String): Exception(message)


// Кидаем при ошибке 409 на любое действие
class ConflictException(message: String): Exception(message)


// Кидаем при 422
class NetworkFormatException(message: String): Exception(message)


class CartException(message: String): Exception(message)


// Кидаем если не подходит ничего из предыдущего
class IDontKnowWhatsHappenedException(message: String): Exception(message)