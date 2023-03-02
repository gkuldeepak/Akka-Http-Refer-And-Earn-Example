package com.knoldus.dao

import com.knoldus.models.UserMappedReferralCode

import scala.concurrent.Future

trait UserReferralCodeMappingRepository {

  def store(userMappedReferralCode: UserMappedReferralCode): Future[Boolean]

}
