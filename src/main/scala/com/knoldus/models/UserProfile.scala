package com.knoldus.models

final case class UserProfile (id: Long, firstName: String, lastName: String,
                              email: String, password: String, refererCode: String, points: Long)
