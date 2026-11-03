package com.example.bookshopapp.data.repository

import com.example.bookshopapp.data.AuthException
import com.example.bookshopapp.data.ConflictException
import com.example.bookshopapp.data.IDontKnowWhatsHappenedException
import com.example.bookshopapp.data.InternetException
import com.example.bookshopapp.data.NotFoundException
import com.example.bookshopapp.data.remote.api.CommentApiService
import com.example.bookshopapp.data.remote.dto.CommentDTO
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okio.IOException
import retrofit2.HttpException
import kotlin.coroutines.cancellation.CancellationException

interface CommentsRepository {
    suspend fun getComments(bookId: Int): List<CommentDTO>
    suspend fun getCommentMe(bookId: Int): CommentDTO
    suspend fun createComment(bookId: Int, text: String)
    suspend fun deleteComment(bookId: Int)


}

class NetworkCommentRepository(
    private  val commentApiService: CommentApiService
): CommentsRepository {
    val mutex = Mutex()

    override suspend fun getComments(bookId: Int): List<CommentDTO> = mutex.withLock {
        try {
            commentApiService.getComments(bookId)
        } catch (e: CancellationException) {
            throw e
        } catch (e: IOException) {
            throw InternetException("Bad internet")
        } catch (e: HttpException) {

            if (e.code() == 404) {
                throw NotFoundException("Book doesnt found")
            } else {
                throw IDontKnowWhatsHappenedException("IDK")
            }

        } catch (e: Exception) {
            throw IDontKnowWhatsHappenedException("IDK")
        }
    }



    override suspend fun getCommentMe(bookId: Int): CommentDTO = mutex.withLock {
        try {
            commentApiService.getCommentMe(bookId)
        } catch (e: CancellationException) {
            throw e
        } catch (e: IOException) {
            throw InternetException("Bad internet")
        } catch (e: HttpException) {
            if (e.code() == 403) {
                throw AuthException("You are unathorize")
            } else if (e.code() == 404) {
                throw NotFoundException("Comment doesnt found")
            } else {
                throw IDontKnowWhatsHappenedException("IDK")
            }

        } catch (e: Exception) {
            throw IDontKnowWhatsHappenedException("IDK")
        }
    }



    override suspend fun createComment(bookId: Int, text: String) {
        mutex.withLock {

            try {
                commentApiService.createComment(bookId, text)
            } catch (e: CancellationException) {
                throw e
            } catch (e: IOException) {
                throw InternetException("Bad internet")
            } catch (e: HttpException) {
                if (e.code() == 403) {
                    throw AuthException("You are unathorize")
                } else if (e.code() == 409) {
                    throw ConflictException("Comment on book already exists")
                } else
                 {
                    throw IDontKnowWhatsHappenedException("IDK")
                }

            } catch (e: Exception) {
                throw IDontKnowWhatsHappenedException("IDK")
            }
        }
    }

    override suspend fun deleteComment(bookId: Int) {
        mutex.withLock {
            try {
                commentApiService.deleteComment(bookId)
            } catch (e: CancellationException) {
                throw e
            } catch (e: IOException) {
                throw InternetException("Bad internet")
            } catch (e: HttpException) {
                if (e.code() == 403) {
                    throw AuthException("You are unathorize")
                } else if (e.code() == 404) {
                    throw NotFoundException("Comment doesnt found")
                } else {
                    throw IDontKnowWhatsHappenedException("IDK")
                }
            } catch (e: Exception) {
                throw IDontKnowWhatsHappenedException("IDK")
            }

        }
    }
}