package chan

import scalaz.effect.IO
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.DelimiterBasedFrameDecoder
import io.netty.handler.codec.Delimiters
import io.netty.handler.codec.string.StringDecoder
import io.netty.handler.codec.string.StringEncoder
import io.netty.util.CharsetUtil.{ UTF_8 => Encoding }
import io.netty.channel.ChannelPipeline
import io.netty.channel.ChannelHandler
import util.TWorld

/** A Netty channel initializer that sets up each new channel as it's opened. */
class ServerChannelInitializer(initialState: => ServerChannelState) extends ChannelInitializer[SocketChannel] {

  import SocketChannelWorld.PipelineWorld._ // see below

  // Our pipeline elements
  val Decoder = new StringDecoder(Encoding)
  val Encoder = new StringEncoder(Encoding)
  val Handler = new ServerChannelHandler(initialState)

  /** Netty callback is delegated to our action (defined below) */
  def initChannel(ch: SocketChannel): Unit =
    initChannelAction.run(ch).unsafePerformIO

  /** An action to construct a frame decoder. This is a stateful object so construction is impure. */
  val frameDecoder: Action[ChannelHandler] =
    IO(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter: _*)).liftIO[Action]

  /** Our action that initializes each new channed. */
  val initChannelAction: Action[Unit] =
    for {
      d <- frameDecoder
      _ <- addLast("framer", d)
      _ <- addLast("decoder", Decoder)
      _ <- addLast("encoder", Encoder)
      _ <- addLast("handler", Handler)
    } yield ()

}

/** 
 * An effect world that knows how to configure a `SocketChannel`. This is a nice example of a very
 * localized and very simple effect world.
 */
object SocketChannelWorld extends TWorld {
  protected type State = SocketChannel

  // This world's actions are of type WorldT[IO, SocketChannel, A]
  object PipelineWorld extends Lifted[IO] {

    /** Action to append a named `ChannelHandler`. */
    def addLast(s: String, cp: ChannelHandler): Action[Unit] =
      effect { ch => ch.pipeline.addLast(s, cp) }

    implicit class Ops[A](a: Action[A]) {
      def run(chc: SocketChannel): IO[A] =
        eval(a, chc).map(_._2)
    }

  }

}

