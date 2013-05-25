mud
===

Experimental pure functional, nonblocking port of bits of DikuMUD.

So far you can log in and walk around and see other players. That's it. But if this kind of thing excites you, all you
should need to do is `sbt run` and then `telnet localhost 6011` in a few windows. Directions and `look` are the only
commands so far.
    

Implementation Notes
--------------------

This is mostly an experiment in applying `tiny-worlds` to something nontrivial, and so far it's working _extremely_ well.
Consructing one-off worlds and world transformers is trivial. `WorldT.Lifted[IO]` instances encapsulate netty and the 
STM-based gamestate, and several one-off worlds encapsulate minor interactions with mutable doodads like channels.

