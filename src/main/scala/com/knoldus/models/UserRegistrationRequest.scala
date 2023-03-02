package com.knoldus.models

final case class PrimaryInfo(firstName: String, lastName: String)

final case class UserRegistrationRequest(primaryInfo: PrimaryInfo, email: String, password: String, referralCode: String)
