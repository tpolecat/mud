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

/** Line-based telnet server, with a state machine for each connextion. */
object Server {

  /**  
   * Action to start the server on a specified port, with an initial connection state. This action
   * blocks forever when executed.
   */
  def run(port: Int, initialState: => ServerChannelState): IO[Unit] =
    for {
      _ <- Log.info(s"Netty server starting on port $port ... ^C to shutdown.")
      _ <- IO {
        val bossGroup, workerGroup = new NioEventLoopGroup
        new ServerBootstrap()
          .group(bossGroup, workerGroup)
          .channel(classOf[NioServerSocketChannel])
          .childHandler(new ServerChannelInitializer(initialState))
          .localAddress(port)
          .bind()
          .sync()
          .channel()
          .closeFuture()
          .sync() // Never returns.
      }
    } yield ()

}
