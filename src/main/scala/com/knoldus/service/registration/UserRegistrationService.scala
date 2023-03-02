package com.knoldus.service.registration

import com.knoldus.dao.{UserDataRepository, UserReferralCodeMappingRepository}
import com.knoldus.models.{ErrorResponse, UserMappedReferralCode, UserProfile, UserRegistrationRequest}

import java.util.Random
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UserRegistrationService(userDataRepository: UserDataRepository,
                              userReferralCodeMappingRepository: UserReferralCodeMappingRepository) {

  def register(userRegistrationRequest: UserRegistrationRequest): Future[Either[String, UserProfile]] = {
    userDataRepository.fetchUserDetails(userRegistrationRequest.email).flatMap {
      case Some(profile) => Future.successful(Left(s"User Already Exist ${profile}"))
      case None => createUser(userRegistrationRequest)
    }
  }

  private def createUser(userRegistrationRequest: UserRegistrationRequest)  = {
    val referralCode = createReferralCode(userRegistrationRequest)
    val userProfile = UserProfile(0, userRegistrationRequest.primaryInfo.firstName, userRegistrationRequest.primaryInfo.lastName,
      userRegistrationRequest.email, userRegistrationRequest.password, referralCode, 20L)
    userDataRepository.store(userProfile).flatMap{
      case profile: UserProfile => rewardUser(userRegistrationRequest, profile)
      case _ => Future.successful(Left("Error while Storing User Details"))
    }
  }

  private def rewardUser(request: UserRegistrationRequest, profile: UserProfile): Future[Either[String, UserProfile]] = {
    if(request.referralCode.nonEmpty){
      userDataRepository.getUserByCode(request.referralCode).flatMap{
        case Some(email) => userDataRepository.updateUserPoints(email, 10L).flatMap{
          case true => mapUserToReward(request, profile)
          case false => Future.successful(Left("Error while Assigning Points"))
        }
        case None => Future.successful(Left("In Valid Referral"))
      }
    }
    else {
      Future.successful(Right(profile))
    }
  }

  private def createReferralCode(userRegistrationRequest: UserRegistrationRequest): String = {
    val uniqueNumber = new Random
    val uniqueNumberString = uniqueNumber.nextInt(100).toString
    val randomCharacter: Char = {
      val low = 65 // A
      val high = 90 // Z

      (uniqueNumber.nextInt(high - low) + low).toChar
    }
    userRegistrationRequest.email.split("@")(0).replaceAll(".","-") +
      uniqueNumberString + randomCharacter.toString + userRegistrationRequest.primaryInfo.lastName +
      randomCharacter.toString + userRegistrationRequest.primaryInfo.firstName
  }

  private def mapUserToReward(request: UserRegistrationRequest, profile: UserProfile): Future[Either[String, UserProfile]] = {
    val userReferralMapped = UserMappedReferralCode(0, request.email, request.referralCode)
    userReferralCodeMappingRepository.store(userReferralMapped).map{
      case true => Right(profile)
      case false => Left("Error while mapping referral to email")
    }
  }
}
