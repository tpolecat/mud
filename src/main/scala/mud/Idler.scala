package mud

import scalaz._
import Scalaz._
import scalaz.effect.IO
import scalaz.effect.IO._
import util.Selector

object Idler {
  
  def start(d:Dungeon): IO[Unit] = {
    import d.state._
    Selector {
      for {
        a <- d.state.allAvatars.run.map(_.values.toList)
        _ <- a.traverse(_.report(Idle))
      } yield (None, 1000)
    }
  }

}