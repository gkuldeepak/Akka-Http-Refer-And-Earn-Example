package com.knoldus.routes.registration

import akka.http.scaladsl.model.{HttpEntity, HttpResponse, MediaTypes, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import com.knoldus.routes.BaseRoutes
import com.knoldus.service.registration.UserRegistrationService
import com.typesafe.config.Config
import com.knoldus.routes.AuthorizationRoutes
import com.knoldus.models.HttpProtocols._
import com.knoldus.models.UserRegistrationRequest

import scala.concurrent.ExecutionContext

class RegistrationRoutes(val conf: Config, userRegistrationService: UserRegistrationService)
                        (implicit
                        val ec: ExecutionContext,
                        val mat: Materializer
                       ) extends BaseRoutes with AuthorizationRoutes{
  val routes : Route =
    path("register") {
      authenticate {
        (post & entity(as[UserRegistrationRequest])){ request =>
          onSuccess(userRegistrationService.register(request)) {
            case Right(response) =>
              complete(HttpResponse(StatusCodes.OK, entity = HttpEntity(MediaTypes.`application/json`, response.toString)))
            case Left(response) =>
              complete(HttpResponse(StatusCodes.BadRequest, entity = HttpEntity(MediaTypes.`application/json`, response.toString)))
          }
        }
      }
    }


}
