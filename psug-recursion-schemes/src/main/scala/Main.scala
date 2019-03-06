import cats.Functor
import cats.effect._
import cats.syntax.functor._
import hammock.asynchttpclient.AsyncHttpClientInterpreter
import hammock.circe.implicits._
import hammock.hi.Opts
import hammock.{Hammock, _}
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.auto._
import org.asynchttpclient.{AsyncHttpClient, DefaultAsyncHttpClient}

case class Chest(id: String, status: String)

object Main {
  implicit val config: Configuration = Configuration.default.withSnakeCaseMemberNames

  val baseUrl = "castles.mediarithmics.randomizer.fr"

  val entry = "/castles/1/rooms/entry"

  val treasureFound = "It looks like there is something here!"

  val client: AsyncHttpClient = new DefaultAsyncHttpClient()
  implicit val httpInterpreter: AsyncHttpClientInterpreter[IO] = new AsyncHttpClientInterpreter[IO](client)

  def httpGetJSON[A: Decoder](id: String): A =
    Hammock.getWithOpts(
      Uri(Some("https"), None, s"$baseUrl$id", Map.empty, None),
      Opts.empty
    ).as[A]
      .exec[IO]
      .unsafeRunSync()


  case class Fix[F[_]](unFix: F[Fix[F]])

  def hylo[F[_] : Functor, A, B](build: A => F[A])(consume: F[B] => B)(a: A): B =
    consume(build(a).map[B](hylo[F, A, B](build)(consume)))





  case class RoomF[+R](id: String, chests: List[String], rooms: List[R])

  object RoomF {
    implicit val functorRoomF: Functor[RoomF] =
      new Functor[RoomF] {
        override def map[A, B](fa: RoomF[A])(f: A => B): RoomF[B] =
          RoomF(fa.id, fa.chests, fa.rooms.map(f))
      }
  }







  def getAllTreasures(entry: String): Int = {
    def buildRoom(roomUrl: String): RoomF[String] =
      httpGetJSON[RoomF[String]](roomUrl)

    def consumeRoom(currentRoom: RoomF[Int]): Int = {
      val currentRoomChests = currentRoom.chests.map(httpGetJSON[Chest])
      val currentRoomTreasures = currentRoomChests.count(c => c.status == treasureFound)
      if (currentRoomTreasures != 0) println("Found something !") else println("Found nothing.")
      currentRoomTreasures + currentRoom.rooms.sum
    }

    hylo(buildRoom)(consumeRoom)(entry)
  }






  def main(args: Array[String]): Unit = {
    val result = getAllTreasures(entry)
    println(result)
  }
}