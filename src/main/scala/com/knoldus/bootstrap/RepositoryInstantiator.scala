package com.knoldus.bootstrap

import com.knoldus.dao.UserDataRepository
import com.knoldus.dao.UserReferralCodeMappingRepository

trait RepositoryInstantiator {

  val userDataRepository: UserDataRepository
  val userReferralCodeMappingRepository: UserReferralCodeMappingRepository

}
