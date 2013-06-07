package chan

import java.net.SocketAddress
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelHandlerContext
import io.netty.util.AttributeKey
import scalaz.effect.IO
import util.TWorld

object ChannelHandlerContextWorld extends TWorld {
  protected type State = ChannelHandlerContext
}

/** An effect world for low-level server operations. */
object ServerChannelWorld extends ChannelHandlerContextWorld.Lifted[IO] {

  /** Write a string to the channel. */
  def write(s: String): Action[Unit] =
    effect(_.write(s))

  /** Write a string plus newline to the channel. */
  def writeLn(s: String): Action[Unit] =
    write(s + "\r\n")

  /** Write a line of text and then close the channel. */
  def kick(s: String): Action[Unit] =
    effect { _.write(s + "\r\n").addListener(ChannelFutureListener.CLOSE) }

  /** Action constructor for an arbitary value. */
  def unit[A](a: A): Action[A] =
    super.unit(a)

  /** Close the channel. */
  def close: Action[Unit] =
    effect(_.close())

  /** Return a constructor of IO actions that write lines to the channel. */
  def writer: Action[String => IO[Unit]] =
    effect(c => s => writeLn(s).run(c))

  /** Return the address associated wit hthe channel. */
  def remoteAddress: Action[SocketAddress] =
    effect(_.channel.remoteAddress)

  /** An `Action` can be run by supplying a `ChannelHandlerContext`. */
  implicit class Ops[A](a: Action[A]) {
    def run(chc: ChannelHandlerContext): IO[A] =
      eval(a, chc).map(_._2)
  }

  /** Private action to get a Netty channel attributes. */
  private def getAttr[A](a: AttributeKey[A]): Action[Option[A]] =
    effect(c => Option(c.channel().attr(a).get()))

  /** Private action to set a Netty channel attributes. */
  private def putAttr[A](k: AttributeKey[A], a: A): Action[Unit] =
    effect(c => c.channel().attr(k).set(a))

  /** Our state has a privileged (private) attribute key. */
  private val StateKey = new AttributeKey[ServerChannelState](classOf[ServerChannelState].getName)

  /** Get our channel state (package-private) */
  private[chan] def getState: Action[ServerChannelState] =
    getAttr(StateKey).map(_.getOrElse(ServerChannelState.Closed)) // TODO: sensible?

  /** Set our channel state (package-private) */
  private[chan] def putState(s: ServerChannelState): Action[ServerChannelState] =
    putAttr(StateKey, s).map(_ => s)

}