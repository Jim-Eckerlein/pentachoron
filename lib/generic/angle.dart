import 'dart:math' as math;

import 'package:meta/meta.dart';

@immutable
class Angle implements Comparable<Angle> {
  final double _storage;

  /// Create a zero angle.
  const Angle.zero() : _storage = 0.0;

  /// Create an angle defined by degrees.
  /// One full turn equals 360 degrees.
  const Angle.fromDegrees(final double degrees)
      : _storage = degrees / 180.0 * math.pi;

  /// Create an angle from radians.
  /// One full turn equals 2pi radians.
  const Angle.fromRadians(final double radians) : _storage = radians;

  /// Create an angle defined by gradians.
  /// One full turn equals 400 gradians.
  const Angle.fromGradians(final double gradians)
      : _storage = gradians / 200.0 * math.pi;

  /// Create an angle defined by turns.
  /// One full turn equals 1 turn.
  const Angle.fromTurns(final double turns) : _storage = turns * 2.0 * math.pi;

  double get turns => (_storage) / math.pi / 2.0;

  double get degrees => (_storage / math.pi) * 180.0;

  double get radians => _storage;
  
  double get gradians => (_storage / math.pi) * 200.0;

  Angle operator +(final Angle other) =>
      Angle.fromRadians(radians + other.radians);

  Angle operator -(final Angle other) =>
      Angle.fromRadians(radians - other.radians);

  Angle operator -() => Angle.fromRadians(-radians);

  Angle operator *(final double scale) => Angle.fromRadians(radians * scale);

  Angle operator /(final double scale) => Angle.fromRadians(radians / scale);

  @override
  String toString() => "$degrees°";

  String toStringRadians() => "${radians}rad";

  bool operator <(final Angle other) => _storage < other._storage;

  bool operator >(final Angle other) => !(this < other) && this != other;

  @override
  int compareTo(Angle other) => this == other ? 0 : this > other ? 1 : -1;
}
