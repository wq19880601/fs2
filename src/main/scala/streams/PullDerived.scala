package streams

trait PullDerived { self: streams.Pull.type =>

  def map[F[_],W,R0,R](p: Pull[F,W,R0])(f: R0 => R): Pull[F,W,R] =
    flatMap(p)(f andThen pure)

  /** Write a single `W` to the output of this `Pull`. */
  def write1[F[_],W](w: W): Pull[F,W,Unit] = writes(Stream.emit(w))

  /** Write a `Chunk[W]` to the output of this `Pull`. */
  def write[F[_],W](w: Chunk[W]): Pull[F,W,Unit] = writes(Stream.chunk(w))

  implicit class PullSyntax[+F[_],+W,+R](p: Pull[F,W,R]) {

    def or[F2[x]>:F[x],W2>:W,R2>:R](p2: => Pull[F2,W2,R2])(
      implicit W2: RealType[W2], R2: RealType[R2])
      : Pull[F2,W2,R2]
      = self.or(p, p2)

    def map[R2](f: R => R2): Pull[F,W,R2] =
      self.map(p)(f)

    def flatMap[F2[x]>:F[x],W2>:W,R2](f: R => Pull[F2,W2,R2]): Pull[F2,W2,R2] =
      self.flatMap(p: Pull[F2,W2,R])(f)
  }
}
