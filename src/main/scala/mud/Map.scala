package mud

// import scala.concurrent.stm._
// import util.M2O
// import util.World
// import scalaz.effect.IO
// import scalaz._
// import Scalaz._

class WorldMap private (map: Map[Room, Map[Direction, Door]], limbo: Room, start: Room) {
  // TODO: check consistency of map, limbo, and start
}

object WorldMap {


  def apply(map: Map[Room, Map[Direction, Door]], limbo: String, start: String) {
    // Find limbo and our starting room; we need them. Diverge on failure.
    // TODO: return a validation
    val limboRoom = map.keys.find(_.name == limbo).getOrElse(sys.error(s"Fatal: can't find $limbo"))
    val startRoom = map.keys.find(_.name == start).getOrElse(sys.error(s"Fatal: can't find $start"))
    new WorldMap(map, limboRoom, startRoom)
  }

}
