package chan

import java.net.SocketAddress
import java.util.logging.Level
import java.util.logging.Logger

import scalaz.effect.IO

import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelHandlerContext
import io.netty.util.AttributeKey
import util.TWorld

object ChannelHandlerContextWorld extends TWorld {
  protected type State = ChannelHandlerContext
}

/** An effect world for low-level server operations. */
object ServerChannelWorld extends ChannelHandlerContextWorld.Lifted[IO] {

  def write(s: String): Action[Unit] =
    effect(_.write(s))

  def writeLn(s: String): Action[Unit] =
    write(s + "\r\n")

  def kick(s: String): Action[Unit] =
    effect { _.write(s + "\r\n").addListener(ChannelFutureListener.CLOSE) }

  def unit[A](a: A): Action[A] =
    super.unit(a)

  def close: Action[Unit] =
    effect(_.close())

  def writer: Action[String => IO[Unit]] =
    effect(c => s => writeLn(s).run(c))

  def remoteAddress: Action[SocketAddress] =
    effect(_.channel.remoteAddress)

  ///

  implicit class Ops[A](a: Action[A]) {
    def run(chc: ChannelHandlerContext): IO[A] =
      eval(a, chc).map(_._2)
  }

  /// STATE MANAGEMENT IS PACKAGE-PRIVATE

  private val StateKey = new AttributeKey[SessionState](classOf[SessionState].getName)

  private[chan] def getAttr[A](a: AttributeKey[A]): Action[Option[A]] =
    effect(c => Option(c.channel().attr(a).get()))

  private[chan] def putAttr[A](k: AttributeKey[A], a: A): Action[Unit] =
    effect(c => c.channel().attr(k).set(a))

  private[chan] def getState: Action[SessionState] =
    getAttr(StateKey).map(_.getOrElse(SessionState.Closed)) // TODO: sensible?

  private[chan] def putState(s: SessionState): Action[SessionState] =
    putAttr(StateKey, s).map(_ => s)

}