package com.knoldus.dao

import com.knoldus.models.UserProfile

import scala.concurrent.Future

trait UserDataRepository {

  def store(userProfile: UserProfile): Future[UserProfile]

  def updateUserProfile(email: String, name: String): Future[Boolean]

  def getUserPoints(email: String): Future[Option[Long]]

  def updateUserPoints(email: String, points: Long): Future[Boolean]

  def fetchUserDetails(email: String): Future[Option[UserProfile]]

  def getUserByCode(code: String): Future[Option[String]]

  def getUserLogIn(email: String, password: String): Future[Option[UserProfile]]
}
