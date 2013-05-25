package chan

import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.{ ChannelInboundMessageHandlerAdapter => Adapter }

@Sharable // Annotation is important
class ServerChannelHandler(initialState: => SessionState) extends Adapter[String] with ServerChannelActions {
  import ServerChannelWorld._
  
  def messageReceived(ctx: ChannelHandlerContext, request: String): Unit =
    messageReceivedAction(request).run(ctx).unsafePerformIO

  override def channelActive(ctx: ChannelHandlerContext): Unit =
    channelActiveAction(initialState).run(ctx).unsafePerformIO

  override def channelInactive(ctx: ChannelHandlerContext): Unit =
    channelInactiveAction.run(ctx).unsafePerformIO

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit =
    exceptionCaughtActon(cause).run(ctx).unsafePerformIO

}