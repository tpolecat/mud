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
import util.TWorld
import io.netty.channel.ChannelPipeline
import io.netty.channel.ChannelHandler

class ServerChannelInitializer(initialState: => SessionState) extends ChannelInitializer[SocketChannel] {

  import SocketChannelWorld.PipelineWorld._

  val Decoder = new StringDecoder(Encoding)
  val Encoder = new StringEncoder(Encoding)
  val Handler = new ServerChannelHandler(initialState)

  def initChannel(ch: SocketChannel): Unit =
    initChannelAction.run(ch).unsafePerformIO

  // Frame decoder is not a sharable RT value, so we need a new one each time.
  val frameDecoder: Action[ChannelHandler] =
    IO(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter: _*)).liftIO[Action]

  val initChannelAction: Action[Unit] =
    for {
      d <- frameDecoder
      _ <- addLast("framer", d)
      _ <- addLast("decoder", Decoder)
      _ <- addLast("encoder", Encoder)
      _ <- addLast("handler", Handler)
    } yield ()

}

// A one-off effect world for socket channels
object SocketChannelWorld extends TWorld {

  protected type State = SocketChannel

  object PipelineWorld extends Lifted[IO] {

    def addLast(s: String, cp: ChannelHandler): Action[Unit] =
      effect { ch => ch.pipeline.addLast(s, cp) }

    implicit class Ops[A](a: Action[A]) {
      def run(chc: SocketChannel): IO[A] =
        eval(a, chc).map(_._2)
    }

  }

}

