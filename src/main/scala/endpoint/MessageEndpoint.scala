package endpoint

import domain.SendMessage
import environment.Environments.MessageServiceEnvironment
import io.circe.generic.auto._
import json.JsonSupportEndpoint
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import scalaz.zio.interop.catz._
import scalaz.zio.{TaskR, ZIO}
import service.MessageServiceImpl

final class MessageEndpoint[R <: MessageServiceEnvironment](rootUri: String)
    extends JsonSupportEndpoint[R] {

  type MessageTask[A] = TaskR[R, A]

  val dsl: Http4sDsl[MessageTask] = Http4sDsl[MessageTask]

  import MessageServiceImpl._
  import dsl._

  def endpoints: HttpRoutes[MessageTask] =
    HttpRoutes.of[MessageTask] {

      case req @ POST -> Root / `rootUri` =>
        val sendMessage: ZIO[R, Throwable, SendMessage] =
          req.as[SendMessage]

        Created(sendMessage >>= publishMessage)

    }
}
