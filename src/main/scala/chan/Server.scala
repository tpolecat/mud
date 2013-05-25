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

object Server {

  def run(port: Int, initialState: => SessionState): IO[Unit] =
    for {
      _ <- Log.info(s"Netty server starting on port $port ... for now just ^c to shutdown.")
      _ <- IO {
        val bossGroup, workerGroup = new NioEventLoopGroup
        try {
          new ServerBootstrap()
            .group(bossGroup, workerGroup)
            .channel(classOf[NioServerSocketChannel])
            .childHandler(new ServerChannelInitializer(initialState))
            .localAddress(port)
            .bind()
            .sync()
            .channel()
            .closeFuture()
            .sync()
        } finally {
          bossGroup.shutdown()
          workerGroup.shutdown()
        }
      }
      _ <- Log.info(s"Netty server exited cleanly.")
    } yield ()

}
