package com.knoldus.db

import com.knoldus.dao.UserReferralCodeMappingRepository
import com.knoldus.models.UserMappedReferralCode
import slick.jdbc.MySQLProfile.api._
import slick.lifted.{ProvenShape, Rep, TableQuery, Tag}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class UserMappedReferralCodeRepositorySqlImpl(db: Database) extends TableQuery(new UserReferralTable(_))
  with UserReferralCodeMappingRepository {

  def store(userMappedReferralCode: UserMappedReferralCode): Future[Boolean] = {
    db.run(this returning this.map(_.id) += userMappedReferralCode) map( _ > 0)
  }

}


class UserReferralTable(tag: Tag) extends Table[UserMappedReferralCode](tag, "user_mapped_code"){
  def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def email: Rep[String] = column[String]("email")

  def referer: Rep[String] = column[String]("refer_code")

  def * : ProvenShape[UserMappedReferralCode] = (id, email, referer) <> (UserMappedReferralCode.tupled , UserMappedReferralCode.unapply)
}