package ru.wexside.misc;

import java.util.Optional;

public interface EnumWireCodec<E> {
   E process(byte var1);

   Optional<E> process2(byte var1);
}
