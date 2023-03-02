package com.knoldus.db

import com.knoldus.dao.UserDataRepository
import com.knoldus.models.UserProfile
import slick.jdbc.MySQLProfile.api._
import slick.lifted.{ProvenShape, Rep, TableQuery, Tag}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UserDataRepositorySqlImpl(db: Database) extends TableQuery(new UserDataTable(_))
  with UserDataRepository {

  def store(userProfile: UserProfile): Future[UserProfile] = {
    db.run(this returning this.map(_.id) += userProfile).mapTo[UserProfile]
  }

  def updateUserProfile(email: String, name: String): Future[Boolean] = {
    db.run(this.filter(_.email === email).map(_.firstName).update(name)) map (_ > 0)
  }

  def getUserPoints(email: String): Future[Option[Long]] = {
    db.run(this.filter(_.email === email).map(_.points).result.headOption)
  }

  def updateUserPoints(email: String, points: Long): Future[Boolean] = {
    val existingPoints = getUserPoints(email)
    existingPoints.flatMap{
      case Some(point) =>
        db.run(this.filter(_.email === email).map(_.points).update(points + point)) map(_ > 0)
      case _ =>
        db.run(this.filter(_.email === email).map(_.points).update(points + 0L)) map(_ > 0)
    }
  }

  def fetchUserDetails(email: String): Future[Option[UserProfile]] = {
    db.run(this.filter(_.email === email).result.headOption)
  }

  def getUserByCode(code: String): Future[Option[String]] = {
    db.run(this.filter(_.referer === code).map(_.email).result.headOption)
  }

  def getUserLogIn(email: String, password: String): Future[Option[UserProfile]] = {
    db.run(this.filter(user => user.email === email && user.password == password ).result.headOption)
  }

}

class UserDataTable(tag: Tag) extends Table[UserProfile](tag, "user_profile"){
  def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def firstName: Rep[String] = column[String]("first_name")

  def lastName: Rep[String] = column[String]("last_name")

  def email: Rep[String] = column[String]("email")

  def password: Rep[String] = column[String]("password")

  def referer: Rep[String] = column[String]("refer_code")

  def points: Rep[Long] = column[Long]("points")

  def * : ProvenShape[UserProfile] = (id, firstName, lastName, email, password, referer, points) <> (UserProfile.tupled , UserProfile.unapply)
}
