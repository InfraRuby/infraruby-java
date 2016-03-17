package primitive;
import org.jruby.Ruby;
import org.jruby.RubyBasicObject;
import org.jruby.RubyObject;
import org.jruby.RubyModule;
import org.jruby.RubyClass;
import org.jruby.RubyNil;
import org.jruby.RubyBoolean;
import org.jruby.RubyInteger;
import org.jruby.RubyFixnum;
import org.jruby.RubyBignum;
import org.jruby.RubyFloat;
import org.jruby.RubyString;
import org.jruby.RubyException;
import org.jruby.anno.JRubyMethod;
import org.jruby.runtime.Block;
import org.jruby.runtime.Helpers;
import org.jruby.runtime.ObjectAllocator;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;
import org.jruby.runtime.load.BasicLibraryService;
import org.jruby.exceptions.RaiseException;
import org.jruby.java.proxies.ConcreteJavaProxy;
import org.jruby.org.objectweb.asm.Label;
import org.jruby.org.objectweb.asm.Opcodes;
import org.jruby.org.objectweb.asm.ClassWriter;
import org.jruby.org.objectweb.asm.FieldVisitor;
import org.jruby.org.objectweb.asm.MethodVisitor;
import org.jcodings.specific.ASCIIEncoding;
import org.jcodings.specific.USASCIIEncoding;
public class PrimitiveService implements BasicLibraryService {
	static final class I32 {
		static Proxy __toString(Ruby runtime, int value) {
			return Gate.getCurrent(runtime).newStringProxy(java.lang.Integer.toString(value));
		}
		static RubyInteger __to_i(Ruby runtime, int value) {
			return RubyFixnum.newFixnum(runtime, (long) value);
		}
		static RubyFloat __to_f(Ruby runtime, int value) {
			return RubyFloat.newFloat(runtime, (double) value);
		}
		static RubyString __to_s(Ruby runtime, int value) {
			return Util.newRubyString(runtime, java.lang.Integer.toString(value), USASCIIEncoding.INSTANCE);
		}
		static RubyString __to_hex(Ruby runtime, int value) {
			int n = 010;
			byte[] bytes = new byte[n];
			while (true) {
				n -= 1;
				int c = (int) value & 0x0F;
				bytes[n] = (byte) (c < 10 ? c + 0x30 : c + 0x41 - 10);
				if (n == 0)
					break;
				value >>= 4;
			}
			return Util.newRubyString(runtime, bytes, USASCIIEncoding.INSTANCE);
		}
		static RubyString __chr(Ruby runtime, int value) {
			if (value < 0x00)
				throw runtime.newRangeError("out of range");
			if (value <= 0x7F)
				return Util.newRubyString(runtime, new byte[] { (byte) value }, USASCIIEncoding.INSTANCE);
			if (value <= 0xFF)
				return Util.newRubyString(runtime, new byte[] { (byte) value }, ASCIIEncoding.INSTANCE);
			throw runtime.newRangeError("out of range");
		}
	}
	static final class I64 {
		static Proxy __toString(Ruby runtime, long value) {
			return Gate.getCurrent(runtime).newStringProxy(java.lang.Long.toString(value));
		}
		static RubyInteger __to_i(Ruby runtime, long value) {
			return RubyFixnum.newFixnum(runtime, (long) value);
		}
		static RubyFloat __to_f(Ruby runtime, long value) {
			return RubyFloat.newFloat(runtime, (double) value);
		}
		static RubyString __to_s(Ruby runtime, long value) {
			return Util.newRubyString(runtime, java.lang.Long.toString(value), USASCIIEncoding.INSTANCE);
		}
		static RubyString __to_hex(Ruby runtime, long value) {
			int n = 020;
			byte[] bytes = new byte[n];
			while (true) {
				n -= 1;
				int c = (int) value & 0x0F;
				bytes[n] = (byte) (c < 10 ? c + 0x30 : c + 0x41 - 10);
				if (n == 0)
					break;
				value >>= 4;
			}
			return Util.newRubyString(runtime, bytes, USASCIIEncoding.INSTANCE);
		}
	}
	static final class F32 {
		static Proxy __toString(Ruby runtime, float value) {
			return Gate.getCurrent(runtime).newStringProxy(java.lang.Float.toString(value));
		}
		static RubyInteger __to_i(Ruby runtime, float value) {
			if (java.lang.Float.isNaN(value))
				throw runtime.newFloatDomainError("NaN");
			if (java.lang.Float.isInfinite(value))
				throw runtime.newFloatDomainError(value < 0 ? "-Infinity" : "Infinity");
			if (value < RubyFixnum.MIN || value >= RubyFixnum.MAX)
				return RubyBignum.newBignum(runtime, value);
			return RubyFixnum.newFixnum(runtime, (long) value);
		}
		static RubyFloat __to_f(Ruby runtime, float value) {
			return RubyFloat.newFloat(runtime, (double) value);
		}
		static RubyString __to_s(Ruby runtime, float value) {
			return Util.newRubyString(runtime, java.lang.Float.toString(value), USASCIIEncoding.INSTANCE);
		}
	}
	static final class F64 {
		static Proxy __toString(Ruby runtime, double value) {
			return Gate.getCurrent(runtime).newStringProxy(java.lang.Double.toString(value));
		}
		static RubyInteger __to_i(Ruby runtime, double value) {
			if (java.lang.Double.isNaN(value))
				throw runtime.newFloatDomainError("NaN");
			if (java.lang.Double.isInfinite(value))
				throw runtime.newFloatDomainError(value < 0 ? "-Infinity" : "Infinity");
			if (value < RubyFixnum.MIN || value >= RubyFixnum.MAX)
				return RubyBignum.newBignum(runtime, value);
			return RubyFixnum.newFixnum(runtime, (long) value);
		}
		static RubyFloat __to_f(Ruby runtime, double value) {
			return RubyFloat.newFloat(runtime, (double) value);
		}
		static RubyString __to_s(Ruby runtime, double value) {
			return Util.newRubyString(runtime, java.lang.Double.toString(value), USASCIIEncoding.INSTANCE);
		}
	}
	public static final class Byte extends JavaValue {
		@JRubyMethod(meta = true, name = "[]")
		public static Byte __get(IRubyObject self, IRubyObject o) {
			Ruby runtime = self.getRuntime();
			byte value = Util.acceptByte(runtime, o);
			return new Byte((RubyClass) self, value);
		}
		final byte value;
		public Byte(RubyClass metaClass, byte value) {
			super(metaClass);
			this.value = value;
			return;
		}
		@JRubyMethod(name = "===")
		public RubyBoolean __eqv(IRubyObject o) {
			return __equals(o);
		}
		@JRubyMethod(name = "equals")
		public RubyBoolean __equals(IRubyObject o) {
			Ruby runtime = getRuntime();
			if (o instanceof Byte)
				if (value == ((Byte) o).value)
					return runtime.getTrue();
			return runtime.getFalse();
		}
		@JRubyMethod(name = "hashCode")
		public Int32 __hashCode() {
			Ruby runtime = getRuntime();
			return new Int32(Gate.getCurrent(runtime).INT32_CLASS, Util.hashInt32(value));
		}
		@JRubyMethod(name = "toString")
		public Proxy __toString() {
			Ruby runtime = getRuntime();
			return I32.__toString(runtime, value);
		}
		@JRubyMethod(name = "eql?")
		public RubyBoolean __is$eql(IRubyObject o) {
			return __equals(o);
		}
		@JRubyMethod(name = "hash")
		public RubyFixnum __hash() {
			Ruby runtime = getRuntime();
			return RubyFixnum.newFixnum(runtime, (long) Util.hashInt32(value));
		}
		@JRubyMethod(name = "to_s")
		public RubyString __to_s() {
			Ruby runtime = getRuntime();
			return I32.__to_s(runtime, value);
		}
		@JRubyMethod(name = "inspect")
		public RubyString __inspect() {
			Ruby runtime = getRuntime();
			return I32.__to_s(runtime, value);
		}
		@JRubyMethod(name = "to_byte")
		public Byte __to_byte() {
			return this;
		}
		@JRubyMethod(name = "to_char")
		public Char __to_char() {
			Ruby runtime = getRuntime();
			return new Char(Gate.getCurrent(runtime).CHAR_CLASS, (char) value);
		}
		@JRubyMethod(name = "to_int16")
		public Int16 __to_int16() {
			Ruby runtime = getRuntime();
			return new Int16(Gate.getCurrent(runtime).INT16_CLASS, (short) value);
		}
		@JRubyMethod(name = "to_int32")
		public Int32 __to_int32() {
			Ruby runtime = getRuntime();
			return new Int32(Gate.getCurrent(runtime).INT32_CLASS, (int) value);
		}
		@JRubyMethod(name = "to_int64")
		public Int64 __to_int64() {
			Ruby runtime = getRuntime();
			return new Int64(Gate.getCurrent(runtime).INT64_CLASS, (long) value);
		}
		@JRubyMethod(name = "to_float32")
		public Float32 __to_float32() {
			Ruby runtime = getRuntime();
			return new Float32(Gate.getCurrent(runtime).FLOAT32_CLASS, (float) value);
		}
		@JRubyMethod(name = "to_float64")
		public Float64 __to_float64() {
			Ruby runtime = getRuntime();
			return new Float64(Gate.getCurrent(runtime).FLOAT64_CLASS, (double) value);
		}
		@JRubyMethod(name = "to_i")
		public RubyInteger __to_i() {
			Ruby runtime = getRuntime();
			return I32.__to_i(runtime, value);
		}
		@JRubyMethod(name = "to_int")
		public RubyInteger __to_int() {
			Ruby runtime = getRuntime();
			return I32.__to_i(runtime, value);
		}
		@JRubyMethod(name = "to_f")
		public RubyFloat __to_f() {
			Ruby runtime = getRuntime();
			return I32.__to_f(runtime, value);
		}
		@JRubyMethod(name = "==")
		public RubyBoolean __eq(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return RubyBoolean.newBoolean(runtime, value == v);
		}
		@JRubyMethod(name = "!=")
		public RubyBoolean __ne(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return RubyBoolean.newBoolean(runtime, value != v);
		}
		@JRubyMethod(name = "+@")
		public Int32 __pos() {
			Ruby runtime = getRuntime();
			return new Int32(Gate.getCurrent(runtime).INT32_CLASS, +value);
		}
		@JRubyMethod(name = "-@")
		public Int32 __neg() {
			Ruby runtime = getRuntime();
			return new Int32(Gate.getCurrent(runtime).INT32_CLASS, -value);
		}
		@JRubyMethod(name = "*")
		public Int32 __mul(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return new Int32(Gate.getCurrent(runtime).INT32_CLASS, value * v);
		}
		@JRubyMethod(name = "/")
		public Int32 __div(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return new Int32(Gate.getCurrent(runtime).INT32_CLASS, value / v);
		}
		@JRubyMethod(name = "%")
		public Int32 __mod(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return new Int32(Gate.getCurrent(runtime).INT32_CLASS, value % v);
		}
		@JRubyMethod(name = "+")
		public Int32 __add(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return new Int32(Gate.getCurrent(runtime).INT32_CLASS, value + v);
		}
		@JRubyMethod(name = "-")
		public Int32 __sub(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return new Int32(Gate.getCurrent(runtime).INT32_CLASS, value - v);
		}
		@JRubyMethod(name = "<=>")
		public Int32 __cmp(IRubyObject o) {
			Ruby runtime = getRuntime();
			Gate gate = Gate.getCurrent(runtime);
			int v = Util.acceptInt32(runtime, o);
			if (value < v)
				return gate.INT32_M1;
			if (value > v)
				return gate.INT32_1;
			return gate.INT32_0;
		}
		@JRubyMethod(name = "<")
		public RubyBoolean __lt(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return RubyBoolean.newBoolean(runtime, value < v);
		}
		@JRubyMethod(name = "<=")
		public RubyBoolean __le(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return RubyBoolean.newBoolean(runtime, value <= v);
		}
		@JRubyMethod(name = ">=")
		public RubyBoolean __ge(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return RubyBoolean.newBoolean(runtime, value >= v);
		}
		@JRubyMethod(name = ">")
		public RubyBoolean __gt(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return RubyBoolean.newBoolean(runtime, value > v);
		}
		@JRubyMethod(name = "~")
		public Int32 __inv() {
			Ruby runtime = getRuntime();
			return new Int32(Gate.getCurrent(runtime).INT32_CLASS, ~value);
		}
		@JRubyMethod(name = "<<")
		public Int32 __shl(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return new Int32(Gate.getCurrent(runtime).INT32_CLASS, value << v);
		}
		@JRubyMethod(name = ">>")
		public Int32 __shr(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return new Int32(Gate.getCurrent(runtime).INT32_CLASS, value >> v);
		}
		@JRubyMethod(name = "ushr")
		public Int32 __ushr(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return new Int32(Gate.getCurrent(runtime).INT32_CLASS, value >>> v);
		}
		@JRubyMethod(name = "&")
		public Int32 __and(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return new Int32(Gate.getCurrent(runtime).INT32_CLASS, value & v);
		}
		@JRubyMethod(name = "^")
		public Int32 __xor(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return new Int32(Gate.getCurrent(runtime).INT32_CLASS, value ^ v);
		}
		@JRubyMethod(name = "|")
		public Int32 __or(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return new Int32(Gate.getCurrent(runtime).INT32_CLASS, value | v);
		}
		@JRubyMethod(name = "zero?")
		public RubyBoolean __is$zero() {
			Ruby runtime = getRuntime();
			return RubyBoolean.newBoolean(runtime, value == 0);
		}
	}
	public static final class Char extends JavaValue {
		@JRubyMethod(meta = true, name = "[]")
		public static Char __get(IRubyObject self, IRubyObject o) {
			Ruby runtime = self.getRuntime();
			char value = Util.acceptChar(runtime, o);
			return new Char((RubyClass) self, value);
		}
		final char value;
		public Char(RubyClass metaClass, char value) {
			super(metaClass);
			this.value = value;
			return;
		}
		@JRubyMethod(name = "===")
		public RubyBoolean __eqv(IRubyObject o) {
			return __equals(o);
		}
		@JRubyMethod(name = "equals")
		public RubyBoolean __equals(IRubyObject o) {
			Ruby runtime = getRuntime();
			if (o instanceof Char)
				if (value == ((Char) o).value)
					return runtime.getTrue();
			return runtime.getFalse();
		}
		@JRubyMethod(name = "hashCode")
		public Int32 __hashCode() {
			Ruby runtime = getRuntime();
			return new Int32(Gate.getCurrent(runtime).INT32_CLASS, Util.hashInt32(value));
		}
		@JRubyMethod(name = "toString")
		public Proxy __toString() {
			Ruby runtime = getRuntime();
			return I32.__toString(runtime, value);
		}
		@JRubyMethod(name = "eql?")
		public RubyBoolean __is$eql(IRubyObject o) {
			return __equals(o);
		}
		@JRubyMethod(name = "hash")
		public RubyFixnum __hash() {
			Ruby runtime = getRuntime();
			return RubyFixnum.newFixnum(runtime, (long) Util.hashInt32(value));
		}
		@JRubyMethod(name = "to_s")
		public RubyString __to_s() {
			Ruby runtime = getRuntime();
			return I32.__to_s(runtime, value);
		}
		@JRubyMethod(name = "inspect")
		public RubyString __inspect() {
			Ruby runtime = getRuntime();
			return I32.__to_s(runtime, value);
		}
		@JRubyMethod(name = "to_byte")
		public Byte __to_byte() {
			Ruby runtime = getRuntime();
			return new Byte(Gate.getCurrent(runtime).BYTE_CLASS, (byte) value);
		}
		@JRubyMethod(name = "to_char")
		public Char __to_char() {
			return this;
		}
		@JRubyMethod(name = "to_int16")
		public Int16 __to_int16() {
			Ruby runtime = getRuntime();
			return new Int16(Gate.getCurrent(runtime).INT16_CLASS, (short) value);
		}
		@JRubyMethod(name = "to_int32")
		public Int32 __to_int32() {
			Ruby runtime = getRuntime();
			return new Int32(Gate.getCurrent(runtime).INT32_CLASS, (int) value);
		}
		@JRubyMethod(name = "to_int64")
		public Int64 __to_int64() {
			Ruby runtime = getRuntime();
			return new Int64(Gate.getCurrent(runtime).INT64_CLASS, (long) value);
		}
		@JRubyMethod(name = "to_float32")
		public Float32 __to_float32() {
			Ruby runtime = getRuntime();
			return new Float32(Gate.getCurrent(runtime).FLOAT32_CLASS, (float) value);
		}
		@JRubyMethod(name = "to_float64")
		public Float64 __to_float64() {
			Ruby runtime = getRuntime();
			return new Float64(Gate.getCurrent(runtime).FLOAT64_CLASS, (double) value);
		}
		@JRubyMethod(name = "to_i")
		public RubyInteger __to_i() {
			Ruby runtime = getRuntime();
			return I32.__to_i(runtime, value);
		}
		@JRubyMethod(name = "to_int")
		public RubyInteger __to_int() {
			Ruby runtime = getRuntime();
			return I32.__to_i(runtime, value);
		}
		@JRubyMethod(name = "to_f")
		public RubyFloat __to_f() {
			Ruby runtime = getRuntime();
			return I32.__to_f(runtime, value);
		}
		@JRubyMethod(name = "==")
		public RubyBoolean __eq(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return RubyBoolean.newBoolean(runtime, value == v);
		}
		@JRubyMethod(name = "!=")
		public RubyBoolean __ne(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return RubyBoolean.newBoolean(runtime, value != v);
		}
		@JRubyMethod(name = "+@")
		public Int32 __pos() {
			Ruby runtime = getRuntime();
			return new Int32(Gate.getCurrent(runtime).INT32_CLASS, +value);
		}
		@JRubyMethod(name = "-@")
		public Int32 __neg() {
			Ruby runtime = getRuntime();
			return new Int32(Gate.getCurrent(runtime).INT32_CLASS, -value);
		}
		@JRubyMethod(name = "*")
		public Int32 __mul(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return new Int32(Gate.getCurrent(runtime).INT32_CLASS, value * v);
		}
		@JRubyMethod(name = "/")
		public Int32 __div(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return new Int32(Gate.getCurrent(runtime).INT32_CLASS, value / v);
		}
		@JRubyMethod(name = "%")
		public Int32 __mod(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return new Int32(Gate.getCurrent(runtime).INT32_CLASS, value % v);
		}
		@JRubyMethod(name = "+")
		public Int32 __add(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return new Int32(Gate.getCurrent(runtime).INT32_CLASS, value + v);
		}
		@JRubyMethod(name = "-")
		public Int32 __sub(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return new Int32(Gate.getCurrent(runtime).INT32_CLASS, value - v);
		}
		@JRubyMethod(name = "<=>")
		public Int32 __cmp(IRubyObject o) {
			Ruby runtime = getRuntime();
			Gate gate = Gate.getCurrent(runtime);
			int v = Util.acceptInt32(runtime, o);
			if (value < v)
				return gate.INT32_M1;
			if (value > v)
				return gate.INT32_1;
			return gate.INT32_0;
		}
		@JRubyMethod(name = "<")
		public RubyBoolean __lt(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return RubyBoolean.newBoolean(runtime, value < v);
		}
		@JRubyMethod(name = "<=")
		public RubyBoolean __le(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return RubyBoolean.newBoolean(runtime, value <= v);
		}
		@JRubyMethod(name = ">=")
		public RubyBoolean __ge(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return RubyBoolean.newBoolean(runtime, value >= v);
		}
		@JRubyMethod(name = ">")
		public RubyBoolean __gt(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return RubyBoolean.newBoolean(runtime, value > v);
		}
		@JRubyMethod(name = "~")
		public Int32 __inv() {
			Ruby runtime = getRuntime();
			return new Int32(Gate.getCurrent(runtime).INT32_CLASS, ~value);
		}
		@JRubyMethod(name = "<<")
		public Int32 __shl(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return new Int32(Gate.getCurrent(runtime).INT32_CLASS, value << v);
		}
		@JRubyMethod(name = ">>")
		public Int32 __shr(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return new Int32(Gate.getCurrent(runtime).INT32_CLASS, value >> v);
		}
		@JRubyMethod(name = "ushr")
		public Int32 __ushr(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return new Int32(Gate.getCurrent(runtime).INT32_CLASS, value >>> v);
		}
		@JRubyMethod(name = "&")
		public Int32 __and(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return new Int32(Gate.getCurrent(runtime).INT32_CLASS, value & v);
		}
		@JRubyMethod(name = "^")
		public Int32 __xor(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return new Int32(Gate.getCurrent(runtime).INT32_CLASS, value ^ v);
		}
		@JRubyMethod(name = "|")
		public Int32 __or(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return new Int32(Gate.getCurrent(runtime).INT32_CLASS, value | v);
		}
		@JRubyMethod(name = "zero?")
		public RubyBoolean __is$zero() {
			Ruby runtime = getRuntime();
			return RubyBoolean.newBoolean(runtime, value == 0);
		}
	}
	public static final class Int16 extends JavaValue {
		@JRubyMethod(meta = true, name = "[]")
		public static Int16 __get(IRubyObject self, IRubyObject o) {
			Ruby runtime = self.getRuntime();
			short value = Util.acceptInt16(runtime, o);
			return new Int16((RubyClass) self, value);
		}
		final short value;
		public Int16(RubyClass metaClass, short value) {
			super(metaClass);
			this.value = value;
			return;
		}
		@JRubyMethod(name = "===")
		public RubyBoolean __eqv(IRubyObject o) {
			return __equals(o);
		}
		@JRubyMethod(name = "equals")
		public RubyBoolean __equals(IRubyObject o) {
			Ruby runtime = getRuntime();
			if (o instanceof Int16)
				if (value == ((Int16) o).value)
					return runtime.getTrue();
			return runtime.getFalse();
		}
		@JRubyMethod(name = "hashCode")
		public Int32 __hashCode() {
			Ruby runtime = getRuntime();
			return new Int32(Gate.getCurrent(runtime).INT32_CLASS, Util.hashInt32(value));
		}
		@JRubyMethod(name = "toString")
		public Proxy __toString() {
			Ruby runtime = getRuntime();
			return I32.__toString(runtime, value);
		}
		@JRubyMethod(name = "eql?")
		public RubyBoolean __is$eql(IRubyObject o) {
			return __equals(o);
		}
		@JRubyMethod(name = "hash")
		public RubyFixnum __hash() {
			Ruby runtime = getRuntime();
			return RubyFixnum.newFixnum(runtime, (long) Util.hashInt32(value));
		}
		@JRubyMethod(name = "to_s")
		public RubyString __to_s() {
			Ruby runtime = getRuntime();
			return I32.__to_s(runtime, value);
		}
		@JRubyMethod(name = "inspect")
		public RubyString __inspect() {
			Ruby runtime = getRuntime();
			return I32.__to_s(runtime, value);
		}
		@JRubyMethod(name = "to_byte")
		public Byte __to_byte() {
			Ruby runtime = getRuntime();
			return new Byte(Gate.getCurrent(runtime).BYTE_CLASS, (byte) value);
		}
		@JRubyMethod(name = "to_char")
		public Char __to_char() {
			Ruby runtime = getRuntime();
			return new Char(Gate.getCurrent(runtime).CHAR_CLASS, (char) value);
		}
		@JRubyMethod(name = "to_int16")
		public Int16 __to_int16() {
			return this;
		}
		@JRubyMethod(name = "to_int32")
		public Int32 __to_int32() {
			Ruby runtime = getRuntime();
			return new Int32(Gate.getCurrent(runtime).INT32_CLASS, (int) value);
		}
		@JRubyMethod(name = "to_int64")
		public Int64 __to_int64() {
			Ruby runtime = getRuntime();
			return new Int64(Gate.getCurrent(runtime).INT64_CLASS, (long) value);
		}
		@JRubyMethod(name = "to_float32")
		public Float32 __to_float32() {
			Ruby runtime = getRuntime();
			return new Float32(Gate.getCurrent(runtime).FLOAT32_CLASS, (float) value);
		}
		@JRubyMethod(name = "to_float64")
		public Float64 __to_float64() {
			Ruby runtime = getRuntime();
			return new Float64(Gate.getCurrent(runtime).FLOAT64_CLASS, (double) value);
		}
		@JRubyMethod(name = "to_i")
		public RubyInteger __to_i() {
			Ruby runtime = getRuntime();
			return I32.__to_i(runtime, value);
		}
		@JRubyMethod(name = "to_int")
		public RubyInteger __to_int() {
			Ruby runtime = getRuntime();
			return I32.__to_i(runtime, value);
		}
		@JRubyMethod(name = "to_f")
		public RubyFloat __to_f() {
			Ruby runtime = getRuntime();
			return I32.__to_f(runtime, value);
		}
		@JRubyMethod(name = "==")
		public RubyBoolean __eq(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return RubyBoolean.newBoolean(runtime, value == v);
		}
		@JRubyMethod(name = "!=")
		public RubyBoolean __ne(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return RubyBoolean.newBoolean(runtime, value != v);
		}
		@JRubyMethod(name = "+@")
		public Int32 __pos() {
			Ruby runtime = getRuntime();
			return new Int32(Gate.getCurrent(runtime).INT32_CLASS, +value);
		}
		@JRubyMethod(name = "-@")
		public Int32 __neg() {
			Ruby runtime = getRuntime();
			return new Int32(Gate.getCurrent(runtime).INT32_CLASS, -value);
		}
		@JRubyMethod(name = "*")
		public Int32 __mul(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return new Int32(Gate.getCurrent(runtime).INT32_CLASS, value * v);
		}
		@JRubyMethod(name = "/")
		public Int32 __div(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return new Int32(Gate.getCurrent(runtime).INT32_CLASS, value / v);
		}
		@JRubyMethod(name = "%")
		public Int32 __mod(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return new Int32(Gate.getCurrent(runtime).INT32_CLASS, value % v);
		}
		@JRubyMethod(name = "+")
		public Int32 __add(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return new Int32(Gate.getCurrent(runtime).INT32_CLASS, value + v);
		}
		@JRubyMethod(name = "-")
		public Int32 __sub(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return new Int32(Gate.getCurrent(runtime).INT32_CLASS, value - v);
		}
		@JRubyMethod(name = "<=>")
		public Int32 __cmp(IRubyObject o) {
			Ruby runtime = getRuntime();
			Gate gate = Gate.getCurrent(runtime);
			int v = Util.acceptInt32(runtime, o);
			if (value < v)
				return gate.INT32_M1;
			if (value > v)
				return gate.INT32_1;
			return gate.INT32_0;
		}
		@JRubyMethod(name = "<")
		public RubyBoolean __lt(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return RubyBoolean.newBoolean(runtime, value < v);
		}
		@JRubyMethod(name = "<=")
		public RubyBoolean __le(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return RubyBoolean.newBoolean(runtime, value <= v);
		}
		@JRubyMethod(name = ">=")
		public RubyBoolean __ge(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return RubyBoolean.newBoolean(runtime, value >= v);
		}
		@JRubyMethod(name = ">")
		public RubyBoolean __gt(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return RubyBoolean.newBoolean(runtime, value > v);
		}
		@JRubyMethod(name = "~")
		public Int32 __inv() {
			Ruby runtime = getRuntime();
			return new Int32(Gate.getCurrent(runtime).INT32_CLASS, ~value);
		}
		@JRubyMethod(name = "<<")
		public Int32 __shl(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return new Int32(Gate.getCurrent(runtime).INT32_CLASS, value << v);
		}
		@JRubyMethod(name = ">>")
		public Int32 __shr(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return new Int32(Gate.getCurrent(runtime).INT32_CLASS, value >> v);
		}
		@JRubyMethod(name = "ushr")
		public Int32 __ushr(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return new Int32(Gate.getCurrent(runtime).INT32_CLASS, value >>> v);
		}
		@JRubyMethod(name = "&")
		public Int32 __and(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return new Int32(Gate.getCurrent(runtime).INT32_CLASS, value & v);
		}
		@JRubyMethod(name = "^")
		public Int32 __xor(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return new Int32(Gate.getCurrent(runtime).INT32_CLASS, value ^ v);
		}
		@JRubyMethod(name = "|")
		public Int32 __or(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return new Int32(Gate.getCurrent(runtime).INT32_CLASS, value | v);
		}
		@JRubyMethod(name = "zero?")
		public RubyBoolean __is$zero() {
			Ruby runtime = getRuntime();
			return RubyBoolean.newBoolean(runtime, value == 0);
		}
	}
	public static final class Int32 extends JavaValue {
		@JRubyMethod(meta = true, name = "[]")
		public static Int32 __get(IRubyObject self, IRubyObject o) {
			Ruby runtime = self.getRuntime();
			int value = Util.acceptInt32(runtime, o);
			return new Int32((RubyClass) self, value);
		}
		final int value;
		public Int32(RubyClass metaClass, int value) {
			super(metaClass);
			this.value = value;
			return;
		}
		@JRubyMethod(name = "===")
		public RubyBoolean __eqv(IRubyObject o) {
			return __equals(o);
		}
		@JRubyMethod(name = "equals")
		public RubyBoolean __equals(IRubyObject o) {
			Ruby runtime = getRuntime();
			if (o instanceof Int32)
				if (value == ((Int32) o).value)
					return runtime.getTrue();
			return runtime.getFalse();
		}
		@JRubyMethod(name = "hashCode")
		public Int32 __hashCode() {
			Ruby runtime = getRuntime();
			return new Int32(getMetaClass(), Util.hashInt32(value));
		}
		@JRubyMethod(name = "toString")
		public Proxy __toString() {
			Ruby runtime = getRuntime();
			return I32.__toString(runtime, value);
		}
		@JRubyMethod(name = "eql?")
		public RubyBoolean __is$eql(IRubyObject o) {
			return __equals(o);
		}
		@JRubyMethod(name = "hash")
		public RubyFixnum __hash() {
			Ruby runtime = getRuntime();
			return RubyFixnum.newFixnum(runtime, (long) Util.hashInt32(value));
		}
		@JRubyMethod(name = "to_s")
		public RubyString __to_s() {
			Ruby runtime = getRuntime();
			return I32.__to_s(runtime, value);
		}
		@JRubyMethod(name = "inspect")
		public RubyString __inspect() {
			Ruby runtime = getRuntime();
			return I32.__to_s(runtime, value);
		}
		@JRubyMethod(name = "to_byte")
		public Byte __to_byte() {
			Ruby runtime = getRuntime();
			return new Byte(Gate.getCurrent(runtime).BYTE_CLASS, (byte) value);
		}
		@JRubyMethod(name = "to_char")
		public Char __to_char() {
			Ruby runtime = getRuntime();
			return new Char(Gate.getCurrent(runtime).CHAR_CLASS, (char) value);
		}
		@JRubyMethod(name = "to_int16")
		public Int16 __to_int16() {
			Ruby runtime = getRuntime();
			return new Int16(Gate.getCurrent(runtime).INT16_CLASS, (short) value);
		}
		@JRubyMethod(name = "to_int32")
		public Int32 __to_int32() {
			return this;
		}
		@JRubyMethod(name = "to_int64")
		public Int64 __to_int64() {
			Ruby runtime = getRuntime();
			return new Int64(Gate.getCurrent(runtime).INT64_CLASS, (long) value);
		}
		@JRubyMethod(name = "to_float32")
		public Float32 __to_float32() {
			Ruby runtime = getRuntime();
			return new Float32(Gate.getCurrent(runtime).FLOAT32_CLASS, (float) value);
		}
		@JRubyMethod(name = "to_float64")
		public Float64 __to_float64() {
			Ruby runtime = getRuntime();
			return new Float64(Gate.getCurrent(runtime).FLOAT64_CLASS, (double) value);
		}
		@JRubyMethod(name = "as_ruby")
		public RubyInteger __as_ruby() {
			Ruby runtime = getRuntime();
			return I32.__to_i(runtime, value);
		}
		@JRubyMethod(name = "as_i")
		public RubyInteger __as_i() {
			Ruby runtime = getRuntime();
			return I32.__to_i(runtime, value);
		}
		@JRubyMethod(name = "to_i")
		public RubyInteger __to_i() {
			Ruby runtime = getRuntime();
			return I32.__to_i(runtime, value);
		}
		@JRubyMethod(name = "to_int")
		public RubyInteger __to_int() {
			Ruby runtime = getRuntime();
			return I32.__to_i(runtime, value);
		}
		@JRubyMethod(name = "to_f")
		public RubyFloat __to_f() {
			Ruby runtime = getRuntime();
			return I32.__to_f(runtime, value);
		}
		@JRubyMethod(name = "==")
		public RubyBoolean __eq(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return RubyBoolean.newBoolean(runtime, value == v);
		}
		@JRubyMethod(name = "!=")
		public RubyBoolean __ne(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return RubyBoolean.newBoolean(runtime, value != v);
		}
		@JRubyMethod(name = "+@")
		public Int32 __pos() {
			return this;
		}
		@JRubyMethod(name = "-@")
		public Int32 __neg() {
			Ruby runtime = getRuntime();
			return new Int32(getMetaClass(), -value);
		}
		@JRubyMethod(name = "*")
		public Int32 __mul(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return new Int32(getMetaClass(), value * v);
		}
		@JRubyMethod(name = "/")
		public Int32 __div(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return new Int32(getMetaClass(), value / v);
		}
		@JRubyMethod(name = "%")
		public Int32 __mod(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return new Int32(getMetaClass(), value % v);
		}
		@JRubyMethod(name = "+")
		public Int32 __add(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return new Int32(getMetaClass(), value + v);
		}
		@JRubyMethod(name = "-")
		public Int32 __sub(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return new Int32(getMetaClass(), value - v);
		}
		@JRubyMethod(name = "<=>")
		public Int32 __cmp(IRubyObject o) {
			Ruby runtime = getRuntime();
			Gate gate = Gate.getCurrent(runtime);
			int v = Util.acceptInt32(runtime, o);
			if (value < v)
				return gate.INT32_M1;
			if (value > v)
				return gate.INT32_1;
			return gate.INT32_0;
		}
		@JRubyMethod(name = "<")
		public RubyBoolean __lt(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return RubyBoolean.newBoolean(runtime, value < v);
		}
		@JRubyMethod(name = "<=")
		public RubyBoolean __le(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return RubyBoolean.newBoolean(runtime, value <= v);
		}
		@JRubyMethod(name = ">=")
		public RubyBoolean __ge(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return RubyBoolean.newBoolean(runtime, value >= v);
		}
		@JRubyMethod(name = ">")
		public RubyBoolean __gt(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return RubyBoolean.newBoolean(runtime, value > v);
		}
		@JRubyMethod(name = "~")
		public Int32 __inv() {
			Ruby runtime = getRuntime();
			return new Int32(getMetaClass(), ~value);
		}
		@JRubyMethod(name = "<<")
		public Int32 __shl(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return new Int32(getMetaClass(), value << v);
		}
		@JRubyMethod(name = ">>")
		public Int32 __shr(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return new Int32(getMetaClass(), value >> v);
		}
		@JRubyMethod(name = "ushr")
		public Int32 __ushr(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return new Int32(getMetaClass(), value >>> v);
		}
		@JRubyMethod(name = "&")
		public Int32 __and(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return new Int32(getMetaClass(), value & v);
		}
		@JRubyMethod(name = "^")
		public Int32 __xor(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return new Int32(getMetaClass(), value ^ v);
		}
		@JRubyMethod(name = "|")
		public Int32 __or(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return new Int32(getMetaClass(), value | v);
		}
		@JRubyMethod(name = "zero?")
		public RubyBoolean __is$zero() {
			Ruby runtime = getRuntime();
			return RubyBoolean.newBoolean(runtime, value == 0);
		}
		@JRubyMethod(name = "to_int32!")
		public Int32 __bang$to_int32() {
			return this;
		}
		@JRubyMethod(name = "to_int64!")
		public Int64 __bang$to_int64() {
			Ruby runtime = getRuntime();
			return new Int64(Gate.getCurrent(runtime).INT64_CLASS, (long) value);
		}
		@JRubyMethod(name = "to_fixnum")
		public RubyFixnum __to_fixnum() {
			Ruby runtime = getRuntime();
			return RubyFixnum.newFixnum(runtime, (long) value);
		}
		@JRubyMethod(name = "new_fixnum")
		public RubyFixnum __new_fixnum() {
			Ruby runtime = getRuntime();
			return RubyFixnum.newFixnum(runtime, (long) value);
		}
		@JRubyMethod(name = "to_hex")
		public RubyString __to_hex() {
			Ruby runtime = getRuntime();
			return I32.__to_hex(runtime, value);
		}
		@JRubyMethod(name = "chr")
		public RubyString __chr() {
			Ruby runtime = getRuntime();
			return I32.__chr(runtime, value);
		}
		@JRubyMethod(name = "succ")
		public Int32 __succ() {
			Ruby runtime = getRuntime();
			if (value != 0x7FFFFFFF)
				return new Int32(getMetaClass(), value + 1);
			throw runtime.newLightweightStopIterationError("StopIteration");
		}
		@JRubyMethod(name = "rol")
		public Int32 __rol(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return new Int32(getMetaClass(), java.lang.Integer.rotateLeft(value, v));
		}
		@JRubyMethod(name = "ror")
		public Int32 __ror(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return new Int32(getMetaClass(), java.lang.Integer.rotateRight(value, v));
		}
		@JRubyMethod(name = "count")
		public Int32 __count() {
			Ruby runtime = getRuntime();
			return new Int32(getMetaClass(), java.lang.Integer.bitCount(value));
		}
		@JRubyMethod(name = "signum")
		public Int32 __signum() {
			Ruby runtime = getRuntime();
			Gate gate = Gate.getCurrent(runtime);
			if (value < 0)
				return gate.INT32_M1;
			if (value > 0)
				return gate.INT32_1;
			return gate.INT32_0;
		}
		@JRubyMethod(name = "times")
		public RubyNil __times(Block block) {
			Ruby runtime = getRuntime();
			ThreadContext context = runtime.getCurrentContext();
			int i = 0;
			while (i < value) {
				block.call(context, new Int32(getMetaClass(), i));
				i += 1;
			}
			return (RubyNil) runtime.getNil();
		}
		@JRubyMethod(name = "upto")
		public RubyNil __upto(IRubyObject o, Block block) {
			Ruby runtime = getRuntime();
			ThreadContext context = runtime.getCurrentContext();
			int i = value;
			int v = Util.acceptInt32(runtime, o);
			if (i <= v) {
				while (true) {
					block.call(context, new Int32(getMetaClass(), i));
					if (i == v)
						break;
					i += 1;
				}
			}
			return (RubyNil) runtime.getNil();
		}
		@JRubyMethod(name = "downto")
		public RubyNil __downto(IRubyObject o, Block block) {
			Ruby runtime = getRuntime();
			ThreadContext context = runtime.getCurrentContext();
			int i = value;
			int v = Util.acceptInt32(runtime, o);
			if (i >= v) {
				while (true) {
					block.call(context, new Int32(getMetaClass(), i));
					if (i == v)
						break;
					i -= 1;
				}
			}
			return (RubyNil) runtime.getNil();
		}
	}
	public static final class Int64 extends JavaValue {
		@JRubyMethod(meta = true, name = "[]")
		public static Int64 __get(IRubyObject self, IRubyObject o) {
			Ruby runtime = self.getRuntime();
			long value = Util.acceptInt64(runtime, o);
			return new Int64((RubyClass) self, value);
		}
		final long value;
		public Int64(RubyClass metaClass, long value) {
			super(metaClass);
			this.value = value;
			return;
		}
		@JRubyMethod(name = "===")
		public RubyBoolean __eqv(IRubyObject o) {
			return __equals(o);
		}
		@JRubyMethod(name = "equals")
		public RubyBoolean __equals(IRubyObject o) {
			Ruby runtime = getRuntime();
			if (o instanceof Int64)
				if (value == ((Int64) o).value)
					return runtime.getTrue();
			return runtime.getFalse();
		}
		@JRubyMethod(name = "hashCode")
		public Int32 __hashCode() {
			Ruby runtime = getRuntime();
			return new Int32(Gate.getCurrent(runtime).INT32_CLASS, Util.hashInt64(value));
		}
		@JRubyMethod(name = "toString")
		public Proxy __toString() {
			Ruby runtime = getRuntime();
			return I64.__toString(runtime, value);
		}
		@JRubyMethod(name = "eql?")
		public RubyBoolean __is$eql(IRubyObject o) {
			return __equals(o);
		}
		@JRubyMethod(name = "hash")
		public RubyFixnum __hash() {
			Ruby runtime = getRuntime();
			return RubyFixnum.newFixnum(runtime, (long) Util.hashInt64(value));
		}
		@JRubyMethod(name = "to_s")
		public RubyString __to_s() {
			Ruby runtime = getRuntime();
			return I64.__to_s(runtime, value);
		}
		@JRubyMethod(name = "inspect")
		public RubyString __inspect() {
			Ruby runtime = getRuntime();
			return I64.__to_s(runtime, value);
		}
		@JRubyMethod(name = "to_byte")
		public Byte __to_byte() {
			Ruby runtime = getRuntime();
			return new Byte(Gate.getCurrent(runtime).BYTE_CLASS, (byte) value);
		}
		@JRubyMethod(name = "to_char")
		public Char __to_char() {
			Ruby runtime = getRuntime();
			return new Char(Gate.getCurrent(runtime).CHAR_CLASS, (char) value);
		}
		@JRubyMethod(name = "to_int16")
		public Int16 __to_int16() {
			Ruby runtime = getRuntime();
			return new Int16(Gate.getCurrent(runtime).INT16_CLASS, (short) value);
		}
		@JRubyMethod(name = "to_int32")
		public Int32 __to_int32() {
			Ruby runtime = getRuntime();
			return new Int32(Gate.getCurrent(runtime).INT32_CLASS, (int) value);
		}
		@JRubyMethod(name = "to_int64")
		public Int64 __to_int64() {
			return this;
		}
		@JRubyMethod(name = "to_float32")
		public Float32 __to_float32() {
			Ruby runtime = getRuntime();
			return new Float32(Gate.getCurrent(runtime).FLOAT32_CLASS, (float) value);
		}
		@JRubyMethod(name = "to_float64")
		public Float64 __to_float64() {
			Ruby runtime = getRuntime();
			return new Float64(Gate.getCurrent(runtime).FLOAT64_CLASS, (double) value);
		}
		@JRubyMethod(name = "as_ruby")
		public RubyInteger __as_ruby() {
			Ruby runtime = getRuntime();
			return I64.__to_i(runtime, value);
		}
		@JRubyMethod(name = "as_i")
		public RubyInteger __as_i() {
			Ruby runtime = getRuntime();
			return I64.__to_i(runtime, value);
		}
		@JRubyMethod(name = "to_i")
		public RubyInteger __to_i() {
			Ruby runtime = getRuntime();
			return I64.__to_i(runtime, value);
		}
		@JRubyMethod(name = "to_int")
		public RubyInteger __to_int() {
			Ruby runtime = getRuntime();
			return I64.__to_i(runtime, value);
		}
		@JRubyMethod(name = "to_f")
		public RubyFloat __to_f() {
			Ruby runtime = getRuntime();
			return I64.__to_f(runtime, value);
		}
		@JRubyMethod(name = "==")
		public RubyBoolean __eq(IRubyObject o) {
			Ruby runtime = getRuntime();
			long v = Util.acceptInt64(runtime, o);
			return RubyBoolean.newBoolean(runtime, value == v);
		}
		@JRubyMethod(name = "!=")
		public RubyBoolean __ne(IRubyObject o) {
			Ruby runtime = getRuntime();
			long v = Util.acceptInt64(runtime, o);
			return RubyBoolean.newBoolean(runtime, value != v);
		}
		@JRubyMethod(name = "+@")
		public Int64 __pos() {
			return this;
		}
		@JRubyMethod(name = "-@")
		public Int64 __neg() {
			Ruby runtime = getRuntime();
			return new Int64(getMetaClass(), -value);
		}
		@JRubyMethod(name = "*")
		public Int64 __mul(IRubyObject o) {
			Ruby runtime = getRuntime();
			long v = Util.acceptInt64(runtime, o);
			return new Int64(getMetaClass(), value * v);
		}
		@JRubyMethod(name = "/")
		public Int64 __div(IRubyObject o) {
			Ruby runtime = getRuntime();
			long v = Util.acceptInt64(runtime, o);
			return new Int64(getMetaClass(), value / v);
		}
		@JRubyMethod(name = "%")
		public Int64 __mod(IRubyObject o) {
			Ruby runtime = getRuntime();
			long v = Util.acceptInt64(runtime, o);
			return new Int64(getMetaClass(), value % v);
		}
		@JRubyMethod(name = "+")
		public Int64 __add(IRubyObject o) {
			Ruby runtime = getRuntime();
			long v = Util.acceptInt64(runtime, o);
			return new Int64(getMetaClass(), value + v);
		}
		@JRubyMethod(name = "-")
		public Int64 __sub(IRubyObject o) {
			Ruby runtime = getRuntime();
			long v = Util.acceptInt64(runtime, o);
			return new Int64(getMetaClass(), value - v);
		}
		@JRubyMethod(name = "<=>")
		public Int32 __cmp(IRubyObject o) {
			Ruby runtime = getRuntime();
			Gate gate = Gate.getCurrent(runtime);
			long v = Util.acceptInt64(runtime, o);
			if (value < v)
				return gate.INT32_M1;
			if (value > v)
				return gate.INT32_1;
			return gate.INT32_0;
		}
		@JRubyMethod(name = "<")
		public RubyBoolean __lt(IRubyObject o) {
			Ruby runtime = getRuntime();
			long v = Util.acceptInt64(runtime, o);
			return RubyBoolean.newBoolean(runtime, value < v);
		}
		@JRubyMethod(name = "<=")
		public RubyBoolean __le(IRubyObject o) {
			Ruby runtime = getRuntime();
			long v = Util.acceptInt64(runtime, o);
			return RubyBoolean.newBoolean(runtime, value <= v);
		}
		@JRubyMethod(name = ">=")
		public RubyBoolean __ge(IRubyObject o) {
			Ruby runtime = getRuntime();
			long v = Util.acceptInt64(runtime, o);
			return RubyBoolean.newBoolean(runtime, value >= v);
		}
		@JRubyMethod(name = ">")
		public RubyBoolean __gt(IRubyObject o) {
			Ruby runtime = getRuntime();
			long v = Util.acceptInt64(runtime, o);
			return RubyBoolean.newBoolean(runtime, value > v);
		}
		@JRubyMethod(name = "~")
		public Int64 __inv() {
			Ruby runtime = getRuntime();
			return new Int64(getMetaClass(), ~value);
		}
		@JRubyMethod(name = "<<")
		public Int64 __shl(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return new Int64(getMetaClass(), value << v);
		}
		@JRubyMethod(name = ">>")
		public Int64 __shr(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return new Int64(getMetaClass(), value >> v);
		}
		@JRubyMethod(name = "ushr")
		public Int64 __ushr(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return new Int64(getMetaClass(), value >>> v);
		}
		@JRubyMethod(name = "&")
		public Int64 __and(IRubyObject o) {
			Ruby runtime = getRuntime();
			long v = Util.acceptInt64(runtime, o);
			return new Int64(getMetaClass(), value & v);
		}
		@JRubyMethod(name = "^")
		public Int64 __xor(IRubyObject o) {
			Ruby runtime = getRuntime();
			long v = Util.acceptInt64(runtime, o);
			return new Int64(getMetaClass(), value ^ v);
		}
		@JRubyMethod(name = "|")
		public Int64 __or(IRubyObject o) {
			Ruby runtime = getRuntime();
			long v = Util.acceptInt64(runtime, o);
			return new Int64(getMetaClass(), value | v);
		}
		@JRubyMethod(name = "zero?")
		public RubyBoolean __is$zero() {
			Ruby runtime = getRuntime();
			return RubyBoolean.newBoolean(runtime, value == 0);
		}
		@JRubyMethod(name = "to_int32!")
		public Int32 __bang$to_int32() {
			Ruby runtime = getRuntime();
			int v = (int) value;
			if (value == (long) v)
				return new Int32(Gate.getCurrent(runtime).INT32_CLASS, v);
			throw runtime.newRangeError("int64 too big to convert to int32");
		}
		@JRubyMethod(name = "to_int64!")
		public Int64 __bang$to_int64() {
			return this;
		}
		@JRubyMethod(name = "to_fixnum")
		public RubyFixnum __to_fixnum() {
			Ruby runtime = getRuntime();
			return RubyFixnum.newFixnum(runtime, (long) value);
		}
		@JRubyMethod(name = "new_fixnum")
		public RubyFixnum __new_fixnum() {
			Ruby runtime = getRuntime();
			return RubyFixnum.newFixnum(runtime, (long) value);
		}
		@JRubyMethod(name = "to_hex")
		public RubyString __to_hex() {
			Ruby runtime = getRuntime();
			return I64.__to_hex(runtime, value);
		}
		@JRubyMethod(name = "succ")
		public Int64 __succ() {
			Ruby runtime = getRuntime();
			if (value != 0x7FFFFFFFFFFFFFFFL)
				return new Int64(getMetaClass(), value + 1);
			throw runtime.newLightweightStopIterationError("StopIteration");
		}
		@JRubyMethod(name = "rol")
		public Int64 __rol(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return new Int64(getMetaClass(), java.lang.Long.rotateLeft(value, v));
		}
		@JRubyMethod(name = "ror")
		public Int64 __ror(IRubyObject o) {
			Ruby runtime = getRuntime();
			int v = Util.acceptInt32(runtime, o);
			return new Int64(getMetaClass(), java.lang.Long.rotateRight(value, v));
		}
		@JRubyMethod(name = "count")
		public Int32 __count() {
			Ruby runtime = getRuntime();
			return new Int32(Gate.getCurrent(runtime).INT32_CLASS, java.lang.Long.bitCount(value));
		}
		@JRubyMethod(name = "signum")
		public Int32 __signum() {
			Ruby runtime = getRuntime();
			Gate gate = Gate.getCurrent(runtime);
			if (value < 0)
				return gate.INT32_M1;
			if (value > 0)
				return gate.INT32_1;
			return gate.INT32_0;
		}
		@JRubyMethod(name = "times")
		public RubyNil __times(Block block) {
			Ruby runtime = getRuntime();
			ThreadContext context = runtime.getCurrentContext();
			long i = 0;
			while (i < value) {
				block.call(context, new Int64(getMetaClass(), i));
				i += 1;
			}
			return (RubyNil) runtime.getNil();
		}
	}
	public static final class Float32 extends JavaValue {
		@JRubyMethod(meta = true, name = "[]")
		public static Float32 __get(IRubyObject self, IRubyObject o) {
			Ruby runtime = self.getRuntime();
			float value = Util.acceptFloat32(runtime, o);
			return new Float32((RubyClass) self, value);
		}
		final float value;
		public Float32(RubyClass metaClass, float value) {
			super(metaClass);
			this.value = value;
			return;
		}
		@JRubyMethod(name = "===")
		public RubyBoolean __eqv(IRubyObject o) {
			return __equals(o);
		}
		@JRubyMethod(name = "equals")
		public RubyBoolean __equals(IRubyObject o) {
			Ruby runtime = getRuntime();
			if (o instanceof Float32)
				if (value == ((Float32) o).value)
					return runtime.getTrue();
			return runtime.getFalse();
		}
		@JRubyMethod(name = "hashCode")
		public Int32 __hashCode() {
			Ruby runtime = getRuntime();
			return new Int32(Gate.getCurrent(runtime).INT32_CLASS, Util.hashFloat32(value));
		}
		@JRubyMethod(name = "toString")
		public Proxy __toString() {
			Ruby runtime = getRuntime();
			return F32.__toString(runtime, value);
		}
		@JRubyMethod(name = "eql?")
		public RubyBoolean __is$eql(IRubyObject o) {
			return __equals(o);
		}
		@JRubyMethod(name = "hash")
		public RubyFixnum __hash() {
			Ruby runtime = getRuntime();
			return RubyFixnum.newFixnum(runtime, (long) Util.hashFloat32(value));
		}
		@JRubyMethod(name = "to_s")
		public RubyString __to_s() {
			Ruby runtime = getRuntime();
			return F32.__to_s(runtime, value);
		}
		@JRubyMethod(name = "inspect")
		public RubyString __inspect() {
			Ruby runtime = getRuntime();
			return F32.__to_s(runtime, value);
		}
		@JRubyMethod(name = "to_byte")
		public Byte __to_byte() {
			Ruby runtime = getRuntime();
			return new Byte(Gate.getCurrent(runtime).BYTE_CLASS, (byte) value);
		}
		@JRubyMethod(name = "to_char")
		public Char __to_char() {
			Ruby runtime = getRuntime();
			return new Char(Gate.getCurrent(runtime).CHAR_CLASS, (char) value);
		}
		@JRubyMethod(name = "to_int16")
		public Int16 __to_int16() {
			Ruby runtime = getRuntime();
			return new Int16(Gate.getCurrent(runtime).INT16_CLASS, (short) value);
		}
		@JRubyMethod(name = "to_int32")
		public Int32 __to_int32() {
			Ruby runtime = getRuntime();
			return new Int32(Gate.getCurrent(runtime).INT32_CLASS, (int) value);
		}
		@JRubyMethod(name = "to_int64")
		public Int64 __to_int64() {
			Ruby runtime = getRuntime();
			return new Int64(Gate.getCurrent(runtime).INT64_CLASS, (long) value);
		}
		@JRubyMethod(name = "to_float32")
		public Float32 __to_float32() {
			return this;
		}
		@JRubyMethod(name = "to_float64")
		public Float64 __to_float64() {
			Ruby runtime = getRuntime();
			return new Float64(Gate.getCurrent(runtime).FLOAT64_CLASS, (double) value);
		}
		@JRubyMethod(name = "as_ruby")
		public RubyFloat __as_ruby() {
			Ruby runtime = getRuntime();
			return F32.__to_f(runtime, value);
		}
		@JRubyMethod(name = "as_f")
		public RubyFloat __as_f() {
			Ruby runtime = getRuntime();
			return F32.__to_f(runtime, value);
		}
		@JRubyMethod(name = "to_i")
		public RubyInteger __to_i() {
			Ruby runtime = getRuntime();
			return F32.__to_i(runtime, value);
		}
		@JRubyMethod(name = "to_int")
		public RubyInteger __to_int() {
			Ruby runtime = getRuntime();
			return F32.__to_i(runtime, value);
		}
		@JRubyMethod(name = "to_f")
		public RubyFloat __to_f() {
			Ruby runtime = getRuntime();
			return F32.__to_f(runtime, value);
		}
		@JRubyMethod(name = "==")
		public RubyBoolean __eq(IRubyObject o) {
			Ruby runtime = getRuntime();
			float v = Util.acceptFloat32(runtime, o);
			return RubyBoolean.newBoolean(runtime, value == v);
		}
		@JRubyMethod(name = "!=")
		public RubyBoolean __ne(IRubyObject o) {
			Ruby runtime = getRuntime();
			float v = Util.acceptFloat32(runtime, o);
			return RubyBoolean.newBoolean(runtime, value != v);
		}
		@JRubyMethod(name = "+@")
		public Float32 __pos() {
			return this;
		}
		@JRubyMethod(name = "-@")
		public Float32 __neg() {
			Ruby runtime = getRuntime();
			return new Float32(getMetaClass(), -value);
		}
		@JRubyMethod(name = "*")
		public Float32 __mul(IRubyObject o) {
			Ruby runtime = getRuntime();
			float v = Util.acceptFloat32(runtime, o);
			return new Float32(getMetaClass(), value * v);
		}
		@JRubyMethod(name = "/")
		public Float32 __div(IRubyObject o) {
			Ruby runtime = getRuntime();
			float v = Util.acceptFloat32(runtime, o);
			return new Float32(getMetaClass(), value / v);
		}
		@JRubyMethod(name = "%")
		public Float32 __mod(IRubyObject o) {
			Ruby runtime = getRuntime();
			float v = Util.acceptFloat32(runtime, o);
			return new Float32(getMetaClass(), value % v);
		}
		@JRubyMethod(name = "+")
		public Float32 __add(IRubyObject o) {
			Ruby runtime = getRuntime();
			float v = Util.acceptFloat32(runtime, o);
			return new Float32(getMetaClass(), value + v);
		}
		@JRubyMethod(name = "-")
		public Float32 __sub(IRubyObject o) {
			Ruby runtime = getRuntime();
			float v = Util.acceptFloat32(runtime, o);
			return new Float32(getMetaClass(), value - v);
		}
		@JRubyMethod(name = "<=>")
		public Int32 __cmp(IRubyObject o) {
			Ruby runtime = getRuntime();
			Gate gate = Gate.getCurrent(runtime);
			float v = Util.acceptFloat32(runtime, o);
			if (value < v)
				return gate.INT32_M1;
			if (value > v)
				return gate.INT32_1;
			return gate.INT32_0;
		}
		@JRubyMethod(name = "<")
		public RubyBoolean __lt(IRubyObject o) {
			Ruby runtime = getRuntime();
			float v = Util.acceptFloat32(runtime, o);
			return RubyBoolean.newBoolean(runtime, value < v);
		}
		@JRubyMethod(name = "<=")
		public RubyBoolean __le(IRubyObject o) {
			Ruby runtime = getRuntime();
			float v = Util.acceptFloat32(runtime, o);
			return RubyBoolean.newBoolean(runtime, value <= v);
		}
		@JRubyMethod(name = ">=")
		public RubyBoolean __ge(IRubyObject o) {
			Ruby runtime = getRuntime();
			float v = Util.acceptFloat32(runtime, o);
			return RubyBoolean.newBoolean(runtime, value >= v);
		}
		@JRubyMethod(name = ">")
		public RubyBoolean __gt(IRubyObject o) {
			Ruby runtime = getRuntime();
			float v = Util.acceptFloat32(runtime, o);
			return RubyBoolean.newBoolean(runtime, value > v);
		}
		@JRubyMethod(name = "zero?")
		public RubyBoolean __is$zero() {
			Ruby runtime = getRuntime();
			return RubyBoolean.newBoolean(runtime, value == 0);
		}
		@JRubyMethod(name = "nan?")
		public RubyBoolean __is$nan() {
			Ruby runtime = getRuntime();
			return RubyBoolean.newBoolean(runtime, java.lang.Float.isNaN(value));
		}
		@JRubyMethod(name = "infinite?")
		public RubyBoolean __is$infinite() {
			Ruby runtime = getRuntime();
			return RubyBoolean.newBoolean(runtime, java.lang.Float.isInfinite(value));
		}
		@JRubyMethod(name = "finite?")
		public RubyBoolean __is$finite() {
			Ruby runtime = getRuntime();
			return RubyBoolean.newBoolean(runtime, !java.lang.Float.isNaN(value) && !java.lang.Float.isInfinite(value));
		}
	}
	public static final class Float64 extends JavaValue {
		@JRubyMethod(meta = true, name = "[]")
		public static Float64 __get(IRubyObject self, IRubyObject o) {
			Ruby runtime = self.getRuntime();
			double value = Util.acceptFloat64(runtime, o);
			return new Float64((RubyClass) self, value);
		}
		final double value;
		public Float64(RubyClass metaClass, double value) {
			super(metaClass);
			this.value = value;
			return;
		}
		@JRubyMethod(name = "===")
		public RubyBoolean __eqv(IRubyObject o) {
			return __equals(o);
		}
		@JRubyMethod(name = "equals")
		public RubyBoolean __equals(IRubyObject o) {
			Ruby runtime = getRuntime();
			if (o instanceof Float64)
				if (value == ((Float64) o).value)
					return runtime.getTrue();
			return runtime.getFalse();
		}
		@JRubyMethod(name = "hashCode")
		public Int32 __hashCode() {
			Ruby runtime = getRuntime();
			return new Int32(Gate.getCurrent(runtime).INT32_CLASS, Util.hashFloat64(value));
		}
		@JRubyMethod(name = "toString")
		public Proxy __toString() {
			Ruby runtime = getRuntime();
			return F64.__toString(runtime, value);
		}
		@JRubyMethod(name = "eql?")
		public RubyBoolean __is$eql(IRubyObject o) {
			return __equals(o);
		}
		@JRubyMethod(name = "hash")
		public RubyFixnum __hash() {
			Ruby runtime = getRuntime();
			return RubyFixnum.newFixnum(runtime, (long) Util.hashFloat64(value));
		}
		@JRubyMethod(name = "to_s")
		public RubyString __to_s() {
			Ruby runtime = getRuntime();
			return F64.__to_s(runtime, value);
		}
		@JRubyMethod(name = "inspect")
		public RubyString __inspect() {
			Ruby runtime = getRuntime();
			return F64.__to_s(runtime, value);
		}
		@JRubyMethod(name = "to_byte")
		public Byte __to_byte() {
			Ruby runtime = getRuntime();
			return new Byte(Gate.getCurrent(runtime).BYTE_CLASS, (byte) value);
		}
		@JRubyMethod(name = "to_char")
		public Char __to_char() {
			Ruby runtime = getRuntime();
			return new Char(Gate.getCurrent(runtime).CHAR_CLASS, (char) value);
		}
		@JRubyMethod(name = "to_int16")
		public Int16 __to_int16() {
			Ruby runtime = getRuntime();
			return new Int16(Gate.getCurrent(runtime).INT16_CLASS, (short) value);
		}
		@JRubyMethod(name = "to_int32")
		public Int32 __to_int32() {
			Ruby runtime = getRuntime();
			return new Int32(Gate.getCurrent(runtime).INT32_CLASS, (int) value);
		}
		@JRubyMethod(name = "to_int64")
		public Int64 __to_int64() {
			Ruby runtime = getRuntime();
			return new Int64(Gate.getCurrent(runtime).INT64_CLASS, (long) value);
		}
		@JRubyMethod(name = "to_float32")
		public Float32 __to_float32() {
			Ruby runtime = getRuntime();
			return new Float32(Gate.getCurrent(runtime).FLOAT32_CLASS, (float) value);
		}
		@JRubyMethod(name = "to_float64")
		public Float64 __to_float64() {
			return this;
		}
		@JRubyMethod(name = "as_ruby")
		public RubyFloat __as_ruby() {
			Ruby runtime = getRuntime();
			return F64.__to_f(runtime, value);
		}
		@JRubyMethod(name = "as_f")
		public RubyFloat __as_f() {
			Ruby runtime = getRuntime();
			return F64.__to_f(runtime, value);
		}
		@JRubyMethod(name = "to_i")
		public RubyInteger __to_i() {
			Ruby runtime = getRuntime();
			return F64.__to_i(runtime, value);
		}
		@JRubyMethod(name = "to_int")
		public RubyInteger __to_int() {
			Ruby runtime = getRuntime();
			return F64.__to_i(runtime, value);
		}
		@JRubyMethod(name = "to_f")
		public RubyFloat __to_f() {
			Ruby runtime = getRuntime();
			return F64.__to_f(runtime, value);
		}
		@JRubyMethod(name = "==")
		public RubyBoolean __eq(IRubyObject o) {
			Ruby runtime = getRuntime();
			double v = Util.acceptFloat64(runtime, o);
			return RubyBoolean.newBoolean(runtime, value == v);
		}
		@JRubyMethod(name = "!=")
		public RubyBoolean __ne(IRubyObject o) {
			Ruby runtime = getRuntime();
			double v = Util.acceptFloat64(runtime, o);
			return RubyBoolean.newBoolean(runtime, value != v);
		}
		@JRubyMethod(name = "+@")
		public Float64 __pos() {
			return this;
		}
		@JRubyMethod(name = "-@")
		public Float64 __neg() {
			Ruby runtime = getRuntime();
			return new Float64(getMetaClass(), -value);
		}
		@JRubyMethod(name = "*")
		public Float64 __mul(IRubyObject o) {
			Ruby runtime = getRuntime();
			double v = Util.acceptFloat64(runtime, o);
			return new Float64(getMetaClass(), value * v);
		}
		@JRubyMethod(name = "/")
		public Float64 __div(IRubyObject o) {
			Ruby runtime = getRuntime();
			double v = Util.acceptFloat64(runtime, o);
			return new Float64(getMetaClass(), value / v);
		}
		@JRubyMethod(name = "%")
		public Float64 __mod(IRubyObject o) {
			Ruby runtime = getRuntime();
			double v = Util.acceptFloat64(runtime, o);
			return new Float64(getMetaClass(), value % v);
		}
		@JRubyMethod(name = "+")
		public Float64 __add(IRubyObject o) {
			Ruby runtime = getRuntime();
			double v = Util.acceptFloat64(runtime, o);
			return new Float64(getMetaClass(), value + v);
		}
		@JRubyMethod(name = "-")
		public Float64 __sub(IRubyObject o) {
			Ruby runtime = getRuntime();
			double v = Util.acceptFloat64(runtime, o);
			return new Float64(getMetaClass(), value - v);
		}
		@JRubyMethod(name = "<=>")
		public Int32 __cmp(IRubyObject o) {
			Ruby runtime = getRuntime();
			Gate gate = Gate.getCurrent(runtime);
			double v = Util.acceptFloat64(runtime, o);
			if (value < v)
				return gate.INT32_M1;
			if (value > v)
				return gate.INT32_1;
			return gate.INT32_0;
		}
		@JRubyMethod(name = "<")
		public RubyBoolean __lt(IRubyObject o) {
			Ruby runtime = getRuntime();
			double v = Util.acceptFloat64(runtime, o);
			return RubyBoolean.newBoolean(runtime, value < v);
		}
		@JRubyMethod(name = "<=")
		public RubyBoolean __le(IRubyObject o) {
			Ruby runtime = getRuntime();
			double v = Util.acceptFloat64(runtime, o);
			return RubyBoolean.newBoolean(runtime, value <= v);
		}
		@JRubyMethod(name = ">=")
		public RubyBoolean __ge(IRubyObject o) {
			Ruby runtime = getRuntime();
			double v = Util.acceptFloat64(runtime, o);
			return RubyBoolean.newBoolean(runtime, value >= v);
		}
		@JRubyMethod(name = ">")
		public RubyBoolean __gt(IRubyObject o) {
			Ruby runtime = getRuntime();
			double v = Util.acceptFloat64(runtime, o);
			return RubyBoolean.newBoolean(runtime, value > v);
		}
		@JRubyMethod(name = "zero?")
		public RubyBoolean __is$zero() {
			Ruby runtime = getRuntime();
			return RubyBoolean.newBoolean(runtime, value == 0);
		}
		@JRubyMethod(name = "nan?")
		public RubyBoolean __is$nan() {
			Ruby runtime = getRuntime();
			return RubyBoolean.newBoolean(runtime, java.lang.Double.isNaN(value));
		}
		@JRubyMethod(name = "infinite?")
		public RubyBoolean __is$infinite() {
			Ruby runtime = getRuntime();
			return RubyBoolean.newBoolean(runtime, java.lang.Double.isInfinite(value));
		}
		@JRubyMethod(name = "finite?")
		public RubyBoolean __is$finite() {
			Ruby runtime = getRuntime();
			return RubyBoolean.newBoolean(runtime, !java.lang.Double.isNaN(value) && !java.lang.Double.isInfinite(value));
		}
	}
	static final class Descriptor {
		final java.lang.String name;
		final java.lang.Class<?>[] parameterTypes;
		final java.lang.Class<?> returnType;
		final java.lang.String desc;
		Descriptor(java.lang.String name, java.lang.Class<?>[] parameterTypes, java.lang.Class<?> returnType) {
			super();
			this.name = name;
			this.parameterTypes = parameterTypes;
			this.returnType = returnType;
			this.desc = ASM.methodDescriptor(parameterTypes, returnType);
			return;
		}
		Descriptor(java.lang.reflect.Constructor constructor) {
			this("<init>", constructor.getParameterTypes(), void.class);
			return;
		}
		Descriptor(java.lang.reflect.Method method) {
			this(method.getName(), method.getParameterTypes(), method.getReturnType());
			return;
		}
		@Override
		public boolean equals(java.lang.Object o) {
			if (o instanceof Descriptor) {
				Descriptor d = (Descriptor) o;
				if (name.equals(d.name) && desc.equals(d.desc))
					return true;
			}
			return false;
		}
		@Override
		public int hashCode() {
			return name.hashCode() ^ desc.hashCode();
		}
	}
	static final class Factory {
		final java.util.HashSet<Descriptor> finalSet;
		final java.lang.Class<?> clazz;
		Factory(java.util.HashSet<Descriptor> finalSet, java.lang.Class<?> clazz) {
			super();
			this.finalSet = finalSet;
			this.clazz = clazz;
			return;
		}
	}
	static final class ASM {
		static java.lang.String referenceDescriptor(java.lang.Class<?> type) {
			if (type.isArray())
				return type.getName().replace('.', '/');
			java.lang.StringBuilder sb = new java.lang.StringBuilder();
			sb.append('L');
			sb.append(type.getName().replace('.', '/'));
			sb.append(';');
			return sb.toString();
		}
		static java.lang.String parameterTypeDescriptor(java.lang.Class<?> type) {
			if (type.isPrimitive()) {
				if (type == boolean.class)
					return "Z";
				if (type == byte.class)
					return "B";
				if (type == char.class)
					return "C";
				if (type == short.class)
					return "S";
				if (type == int.class)
					return "I";
				if (type == long.class)
					return "J";
				if (type == float.class)
					return "F";
				if (type == double.class)
					return "D";
				throw new java.lang.AssertionError();
			}
			return referenceDescriptor(type);
		}
		static java.lang.String returnTypeDescriptor(java.lang.Class<?> type) {
			if (type == void.class)
				return "V";
			return parameterTypeDescriptor(type);
		}
		static java.lang.String methodDescriptor(java.lang.Class<?>[] parameterTypes, java.lang.Class<?> returnType) {
			java.lang.StringBuilder sb = new java.lang.StringBuilder();
			sb.append('(');
			for (java.lang.Class<?> type : parameterTypes)
				sb.append(parameterTypeDescriptor(type));
			sb.append(')');
			sb.append(returnTypeDescriptor(returnType));
			return sb.toString();
		}
		static java.lang.String methodSignature(java.lang.Class<?>[] parameterTypes) {
			java.lang.StringBuilder sb = new java.lang.StringBuilder();
			for (java.lang.Class<?> type : parameterTypes)
				sb.append(parameterTypeDescriptor(type));
			return sb.toString();
		}
		static java.lang.String[] interfaces(java.util.List<java.lang.Class<?>> clazzs) {
			int n = clazzs.size();
			java.lang.String[] interfaces = new java.lang.String[n];
			int i = 0;
			for (java.lang.Class<?> clazz : clazzs)
				interfaces[i++] = clazz.getName().replace('.', '/');
			return interfaces;
		}
		static int category(java.lang.Class<?> type) {
			if (type.isPrimitive()) {
				if (type == boolean.class)
					return 1;
				if (type == byte.class)
					return 1;
				if (type == char.class)
					return 1;
				if (type == short.class)
					return 1;
				if (type == int.class)
					return 1;
				if (type == long.class)
					return 2;
				if (type == float.class)
					return 1;
				if (type == double.class)
					return 2;
				throw new java.lang.AssertionError();
			}
			return 1;
		}
		static int opcodeLoadVar(java.lang.Class<?> type) {
			if (type.isPrimitive()) {
				if (type == boolean.class)
					return Opcodes.ILOAD;
				if (type == byte.class)
					return Opcodes.ILOAD;
				if (type == char.class)
					return Opcodes.ILOAD;
				if (type == short.class)
					return Opcodes.ILOAD;
				if (type == int.class)
					return Opcodes.ILOAD;
				if (type == long.class)
					return Opcodes.LLOAD;
				if (type == float.class)
					return Opcodes.FLOAD;
				if (type == double.class)
					return Opcodes.DLOAD;
				throw new java.lang.AssertionError();
			}
			return Opcodes.ALOAD;
		}
		static int opcodeReturn(java.lang.Class<?> type) {
			if (type.isPrimitive()) {
				if (type == void.class)
					return Opcodes.RETURN;
				if (type == boolean.class)
					return Opcodes.IRETURN;
				if (type == byte.class)
					return Opcodes.IRETURN;
				if (type == char.class)
					return Opcodes.IRETURN;
				if (type == short.class)
					return Opcodes.IRETURN;
				if (type == int.class)
					return Opcodes.IRETURN;
				if (type == long.class)
					return Opcodes.LRETURN;
				if (type == float.class)
					return Opcodes.FRETURN;
				if (type == double.class)
					return Opcodes.DRETURN;
				throw new java.lang.AssertionError();
			}
			return Opcodes.ARETURN;
		}
		static void visitInt32(MethodVisitor mv, int v) {
			switch (v) {
			case -1:
				mv.visitInsn(Opcodes.ICONST_M1);
				break;
			case 0:
				mv.visitInsn(Opcodes.ICONST_0);
				break;
			case 1:
				mv.visitInsn(Opcodes.ICONST_1);
				break;
			case 2:
				mv.visitInsn(Opcodes.ICONST_2);
				break;
			case 3:
				mv.visitInsn(Opcodes.ICONST_3);
				break;
			case 4:
				mv.visitInsn(Opcodes.ICONST_4);
				break;
			case 5:
				mv.visitInsn(Opcodes.ICONST_5);
				break;
			default:
				if (v >= -0x80 && v < 0x80)
					mv.visitIntInsn(Opcodes.BIPUSH, v);
				else if (v >= -0x8000 && v < 0x8000)
					mv.visitIntInsn(Opcodes.SIPUSH, v);
				else
					mv.visitLdcInsn(java.lang.Integer.valueOf(v));
			}
			return;
		}
		static void visitLoadVar(MethodVisitor mv, java.lang.Class<?> type, int var) {
			mv.visitVarInsn(opcodeLoadVar(type), var);
			return;
		}
		static void visitLoadVars(MethodVisitor mv, java.lang.Class<?>[] types) {
			int var = 1;
			for (java.lang.Class<?> type : types) {
				visitLoadVar(mv, type, var);
				var += category(type);
			}
			return;
		}
		static void visitReturn(MethodVisitor mv, java.lang.Class<?> type) {
			mv.visitInsn(opcodeReturn(type));
			return;
		}
		static void visitObjectField(ClassWriter cw) {
			FieldVisitor fv = cw.visitField(0, "o", "Lorg/jruby/RubyBasicObject;", null, null);
			fv.visitEnd();
			return;
		}
		static void visitObjectReader(ClassWriter cw, java.lang.String className) {
			MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL, "__ir_get$object", "()Lorg/jruby/RubyBasicObject;", null, null);
			mv.visitCode();
			mv.visitVarInsn(Opcodes.ALOAD, 0);
			mv.visitFieldInsn(Opcodes.GETFIELD, className, "o", "Lorg/jruby/RubyBasicObject;");
			mv.visitInsn(Opcodes.ARETURN);
			mv.visitMaxs(0, 0);
			mv.visitEnd();
			return;
		}
		static void visitObjectWriter(ClassWriter cw, java.lang.String className) {
			MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL, "__ir_set$object", "(Lorg/jruby/RubyBasicObject;)V", null, null);
			mv.visitCode();
			mv.visitVarInsn(Opcodes.ALOAD, 0);
			mv.visitVarInsn(Opcodes.ALOAD, 1);
			mv.visitFieldInsn(Opcodes.PUTFIELD, className, "o", "Lorg/jruby/RubyBasicObject;");
			mv.visitInsn(Opcodes.RETURN);
			mv.visitMaxs(0, 0);
			mv.visitEnd();
			return;
		}
		static void visitLoadObject(MethodVisitor mv) {
			mv.visitVarInsn(Opcodes.ALOAD, 0);
			mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, Adapter.NAME, "__ir_get$object", "()Lorg/jruby/RubyBasicObject;", true);
			return;
		}
		static void visitValueVar(MethodVisitor mv, java.lang.Class<?> type) {
			if (type.isPrimitive()) {
				if (type == boolean.class) {
					mv.visitMethodInsn(Opcodes.INVOKESTATIC, Trap.NAME, "newBoolean", "(Lorg/jruby/Ruby;Z)Lorg/jruby/runtime/builtin/IRubyObject;", false);
					return;
				}
				if (type == byte.class) {
					mv.visitMethodInsn(Opcodes.INVOKESTATIC, Trap.NAME, "newByte", "(Lorg/jruby/Ruby;B)Lorg/jruby/runtime/builtin/IRubyObject;", false);
					return;
				}
				if (type == char.class) {
					mv.visitMethodInsn(Opcodes.INVOKESTATIC, Trap.NAME, "newChar", "(Lorg/jruby/Ruby;C)Lorg/jruby/runtime/builtin/IRubyObject;", false);
					return;
				}
				if (type == short.class) {
					mv.visitMethodInsn(Opcodes.INVOKESTATIC, Trap.NAME, "newInt16", "(Lorg/jruby/Ruby;S)Lorg/jruby/runtime/builtin/IRubyObject;", false);
					return;
				}
				if (type == int.class) {
					mv.visitMethodInsn(Opcodes.INVOKESTATIC, Trap.NAME, "newInt32", "(Lorg/jruby/Ruby;I)Lorg/jruby/runtime/builtin/IRubyObject;", false);
					return;
				}
				if (type == long.class) {
					mv.visitMethodInsn(Opcodes.INVOKESTATIC, Trap.NAME, "newInt64", "(Lorg/jruby/Ruby;J)Lorg/jruby/runtime/builtin/IRubyObject;", false);
					return;
				}
				if (type == float.class) {
					mv.visitMethodInsn(Opcodes.INVOKESTATIC, Trap.NAME, "newFloat32", "(Lorg/jruby/Ruby;F)Lorg/jruby/runtime/builtin/IRubyObject;", false);
					return;
				}
				if (type == double.class) {
					mv.visitMethodInsn(Opcodes.INVOKESTATIC, Trap.NAME, "newFloat64", "(Lorg/jruby/Ruby;D)Lorg/jruby/runtime/builtin/IRubyObject;", false);
					return;
				}
				throw new java.lang.AssertionError();
			}
			mv.visitMethodInsn(Opcodes.INVOKESTATIC, Trap.NAME, "makeProxyUnlessAdapterMaybe", "(Lorg/jruby/Ruby;Ljava/lang/Object;)Lorg/jruby/runtime/builtin/IRubyObject;", false);
			return;
		}
		static void visitValueVars(MethodVisitor mv, java.lang.Class<?>[] types) {
			int n = types.length;
			visitInt32(mv, n);
			mv.visitTypeInsn(Opcodes.ANEWARRAY, "org/jruby/runtime/builtin/IRubyObject");
			int var = 1;
			for (int i = 0; i < n; i += 1) {
				java.lang.Class<?> type = types[i];
				mv.visitInsn(Opcodes.DUP);
				visitInt32(mv, i);
				visitLoadObject(mv);
				mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/jruby/RubyBasicObject", "getRuntime", "()Lorg/jruby/Ruby;", false);
				visitLoadVar(mv, type, var);
				var += category(type);
				visitValueVar(mv, type);
				mv.visitInsn(Opcodes.AASTORE);
			}
			return;
		}
		static void visitCall(MethodVisitor mv, java.lang.Class<?> type) {
			if (type.isPrimitive()) {
				if (type == void.class) {
					mv.visitMethodInsn(Opcodes.INVOKESTATIC, Trap.NAME, "callVoid", "(Lorg/jruby/RubyBasicObject;Ljava/lang/String;[Lorg/jruby/runtime/builtin/IRubyObject;)V", false);
					mv.visitInsn(Opcodes.RETURN);
					return;
				}
				if (type == boolean.class) {
					mv.visitMethodInsn(Opcodes.INVOKESTATIC, Trap.NAME, "callBoolean", "(Lorg/jruby/RubyBasicObject;Ljava/lang/String;[Lorg/jruby/runtime/builtin/IRubyObject;)Z", false);
					mv.visitInsn(Opcodes.IRETURN);
					return;
				}
				if (type == byte.class) {
					mv.visitMethodInsn(Opcodes.INVOKESTATIC, Trap.NAME, "callByte", "(Lorg/jruby/RubyBasicObject;Ljava/lang/String;[Lorg/jruby/runtime/builtin/IRubyObject;)B", false);
					mv.visitInsn(Opcodes.IRETURN);
					return;
				}
				if (type == char.class) {
					mv.visitMethodInsn(Opcodes.INVOKESTATIC, Trap.NAME, "callChar", "(Lorg/jruby/RubyBasicObject;Ljava/lang/String;[Lorg/jruby/runtime/builtin/IRubyObject;)C", false);
					mv.visitInsn(Opcodes.IRETURN);
					return;
				}
				if (type == short.class) {
					mv.visitMethodInsn(Opcodes.INVOKESTATIC, Trap.NAME, "callInt16", "(Lorg/jruby/RubyBasicObject;Ljava/lang/String;[Lorg/jruby/runtime/builtin/IRubyObject;)S", false);
					mv.visitInsn(Opcodes.IRETURN);
					return;
				}
				if (type == int.class) {
					mv.visitMethodInsn(Opcodes.INVOKESTATIC, Trap.NAME, "callInt32", "(Lorg/jruby/RubyBasicObject;Ljava/lang/String;[Lorg/jruby/runtime/builtin/IRubyObject;)I", false);
					mv.visitInsn(Opcodes.IRETURN);
					return;
				}
				if (type == long.class) {
					mv.visitMethodInsn(Opcodes.INVOKESTATIC, Trap.NAME, "callInt64", "(Lorg/jruby/RubyBasicObject;Ljava/lang/String;[Lorg/jruby/runtime/builtin/IRubyObject;)J", false);
					mv.visitInsn(Opcodes.LRETURN);
					return;
				}
				if (type == float.class) {
					mv.visitMethodInsn(Opcodes.INVOKESTATIC, Trap.NAME, "callFloat32", "(Lorg/jruby/RubyBasicObject;Ljava/lang/String;[Lorg/jruby/runtime/builtin/IRubyObject;)F", false);
					mv.visitInsn(Opcodes.FRETURN);
					return;
				}
				if (type == double.class) {
					mv.visitMethodInsn(Opcodes.INVOKESTATIC, Trap.NAME, "callFloat64", "(Lorg/jruby/RubyBasicObject;Ljava/lang/String;[Lorg/jruby/runtime/builtin/IRubyObject;)D", false);
					mv.visitInsn(Opcodes.DRETURN);
					return;
				}
				throw new java.lang.AssertionError();
			}
			mv.visitMethodInsn(Opcodes.INVOKESTATIC, Trap.NAME, "callReference", "(Lorg/jruby/RubyBasicObject;Ljava/lang/String;[Lorg/jruby/runtime/builtin/IRubyObject;)Ljava/lang/Object;", false);
			mv.visitTypeInsn(Opcodes.CHECKCAST, type.getName().replace('.', '/'));
			mv.visitInsn(Opcodes.ARETURN);
			return;
		}
		static void visitConstructor(ClassWriter cw, java.lang.String superName, Descriptor d) {
			MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", d.desc, null, null);
			mv.visitCode();
			mv.visitVarInsn(Opcodes.ALOAD, 0);
			visitLoadVars(mv, d.parameterTypes);
			mv.visitMethodInsn(Opcodes.INVOKESPECIAL, superName, "<init>", d.desc, false);
			mv.visitInsn(Opcodes.RETURN);
			mv.visitMaxs(0, 0);
			mv.visitEnd();
			return;
		}
		static void visitConstructors(ClassWriter cw, java.lang.String superName, java.lang.Class<?> clazz) {
			java.lang.reflect.Constructor<?>[] constructors = clazz.getDeclaredConstructors();
			for (java.lang.reflect.Constructor<?> constructor : constructors)
				if (Util.isPublic(constructor) || Util.isProtected(constructor))
					visitConstructor(cw, superName, new Descriptor(constructor));
			return;
		}
		static void visitSuper(ClassWriter cw, java.lang.String superName, Descriptor d) {
			MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "__ir_super" + '$' + d.name, d.desc, null, null);
			mv.visitCode();
			mv.visitVarInsn(Opcodes.ALOAD, 0);
			visitLoadVars(mv, d.parameterTypes);
			mv.visitMethodInsn(Opcodes.INVOKESPECIAL, superName, d.name, d.desc, false);
			visitReturn(mv, d.returnType);
			mv.visitMaxs(0, 0);
			mv.visitEnd();
			return;
		}
		static void visitSupers(ClassWriter cw, java.lang.String superName, java.util.HashSet<Descriptor> superSet) {
			for (Descriptor d : superSet)
				visitSuper(cw, superName, d);
			return;
		}
		static void visitVirtual(ClassWriter cw, Descriptor d) {
			MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, d.name, d.desc, null, null);
			mv.visitCode();
			visitLoadObject(mv);
			mv.visitLdcInsn(d.name);
			visitValueVars(mv, d.parameterTypes);
			visitCall(mv, d.returnType);
			mv.visitMaxs(0, 0);
			mv.visitEnd();
			return;
		}
		static void visitVirtuals(ClassWriter cw, java.util.HashSet<Descriptor> virtualSet) {
			for (Descriptor d : virtualSet)
				visitVirtual(cw, d);
			return;
		}
	}
	static final class Multimap<K, V> {
		final java.util.HashMap<K, java.util.HashSet<V>> map;
		Multimap() {
			super();
			map = new java.util.HashMap<K, java.util.HashSet<V>>();
			return;
		}
		void add(K k, V v) {
			java.util.HashSet<V> set = makeSet(k);
			set.add(v);
			return;
		}
		java.util.HashSet<V> get(K k) {
			return map.get(k);
		}
		java.util.HashSet<V> remove(K k) {
			return map.remove(k);
		}
		java.util.HashSet<V> makeSet(K k) {
			java.util.HashSet<V> set = map.get(k);
			if (set == null) {
				set = new java.util.HashSet<V>();
				map.put(k, set);
			}
			return set;
		}
		java.util.Set<K> keySet() {
			return map.keySet();
		}
	}
	static final class MethodMultimap {
		static final class Entry {
			static java.lang.reflect.Method[] methods(java.util.HashSet<Entry> set) {
				int n = set.size();
				java.lang.reflect.Method[] methods = new java.lang.reflect.Method[n];
				java.util.Iterator<Entry> it = set.iterator();
				for (int i = 0; i < n; i += 1)
					methods[i] = it.next().method;
				return methods;
			}
			final java.lang.reflect.Method method;
			final java.lang.String signature;
			Entry(java.lang.reflect.Method method) {
				super();
				this.method = method;
				this.signature = ASM.methodSignature(method.getParameterTypes());
				return;
			}
			@Override
			public boolean equals(java.lang.Object o) {
				if (o instanceof Entry) {
					Entry e = (Entry) o;
					if (signature.equals(e.signature))
						return true;
				}
				return false;
			}
			@Override
			public int hashCode() {
				return signature.hashCode();
			}
		}
		final Multimap<java.lang.String, Entry> map;
		MethodMultimap() {
			super();
			map = new Multimap<java.lang.String, Entry>();
			return;
		}
		void addStaticMethod(java.lang.reflect.Method method) {
			java.lang.String name = method.getName();
			if (Util.isPublic(method))
				map.add(name, new Entry(method));
			return;
		}
		void addVirtualMethod(java.lang.reflect.Method method) {
			java.lang.String name = method.getName();
			if (!Util.isAbstract(method)) {
				if (Util.isPublic(method))
					map.add(name, new Entry(method));
				if (Util.isProtected(method))
					map.makeSet(name);
			}
			return;
		}
		void addSuperMethod(java.lang.reflect.Method method) {
			java.lang.String name = method.getName();
			if (name.startsWith("__ir_super" + '$'))
				map.add(name, new Entry(method));
			return;
		}
		java.util.Set<java.lang.String> keySet() {
			return map.keySet();
		}
		java.lang.reflect.Method[] getMethods(java.lang.String name) {
			java.util.HashSet<Entry> set = map.get(name);
			return Entry.methods(set);
		}
		java.lang.reflect.Method[] removeMethods(java.lang.String name) {
			java.util.HashSet<Entry> set = map.remove(name);
			return Entry.methods(set);
		}
	}
	static abstract class RubyMethod extends org.jruby.internal.runtime.methods.DynamicMethod {
		RubyMethod(RubyModule module) {
			super(module, org.jruby.runtime.Visibility.PUBLIC, org.jruby.internal.runtime.methods.CallConfiguration.FrameFullScopeNone);
			return;
		}
		@Override
		public org.jruby.internal.runtime.methods.DynamicMethod dup() {
			return this;
		}
	}
	static final class J2R {
		final Gate gate;
		final java.util.HashMap<java.lang.Class<?>, RubyModule> moduleMap;
		final java.util.HashMap<java.lang.Class<?>, RubyClass> klassMap;
		J2R(Gate gate) {
			super();
			this.gate = gate;
			moduleMap = new java.util.HashMap<java.lang.Class<?>, RubyModule>();
			klassMap = new java.util.HashMap<java.lang.Class<?>, RubyClass>();
			return;
		}
		private void __includeModules(RubyModule module, java.lang.Class<?>[] clazzs) {
			for (java.lang.Class<?> clazz : clazzs)
				module.includeModule(__makeModule(clazz));
			return;
		}
		private RubyModule __newModule(java.lang.Class<?> clazz) {
			RubyModule module;
			module = RubyModule.newModule(gate.runtime);
			__includeModules(module, clazz.getInterfaces());
			module.setBaseName(clazz.getName().replace("$", "::"));
			module.defineAnnotatedMethods(ClassMixin.class);
			Util.setJavaClass(module, clazz);
			Util.addInterfaceMembers(module, clazz);
			return module;
		}
		private RubyClass __newKlass(java.lang.Class<?> clazz) {
			RubyClass klass;
			RubyClass superklass = __makeSuperklass(clazz);
			klass = RubyClass.newClass(gate.runtime, superklass);
			__includeModules(klass, clazz.getInterfaces());
			klass.setBaseName(clazz.getName().replace("$", "::"));
			klass.makeMetaClass(superklass.getMetaClass());
			klass.defineAnnotatedMethods(ClassMixin.class);
			Util.setJavaClass(klass, clazz);
			if (Util.isPublic(clazz)) {
				Factory factory = Util.isFinal(clazz) ? null : gate.newFactory(clazz);
				Util.setFactory(klass, factory);
				Util.addPublicClassMembers(klass, clazz, factory);
				klass.setAllocator(JavaObject.ALLOCATOR);
			} else {
				Util.setFactory(klass, null);
				Util.addPrivateClassMembers(klass, clazz);
				klass.setAllocator(ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
			}
			return klass;
		}
		private RubyClass __makeSuperklass(java.lang.Class<?> clazz) {
			java.lang.Class<?> superclazz = clazz.getSuperclass();
			if (superclazz == null)
				return gate.JAVA_BASIC_OBJECT_CLASS;
			return __makeKlass(superclazz);
		}
		private RubyModule __makeModule(java.lang.Class<?> clazz) {
			RubyModule module;
			module = moduleMap.get(clazz);
			if (module == null) {
				module = __newModule(clazz);
				moduleMap.put(clazz, module);
			}
			return module;
		}
		private RubyClass __makeKlass(java.lang.Class<?> clazz) {
			RubyClass klass;
			klass = klassMap.get(clazz);
			if (klass == null) {
				klass = __newKlass(clazz);
				klassMap.put(clazz, klass);
			}
			return klass;
		}
		synchronized RubyModule makeModuleOrKlass(java.lang.Class<?> clazz) {
			return clazz.isInterface() ? __makeModule(clazz) : __makeKlass(clazz);
		}
		synchronized RubyClass makeKlass(java.lang.Class<?> clazz) {
			return __makeKlass(clazz);
		}
	}
	static final class R2J {
		final Gate gate;
		final java.util.HashMap<java.lang.Class<?>, RubyModule> moduleMap;
		final java.util.HashMap<java.lang.Class<?>, RubyClass> klassMap;
		final java.util.HashMap<RubyModule, java.lang.Class<?>> interfaceMap;
		final java.util.HashMap<RubyClass, Factory> factoryMap;
		R2J(Gate gate) {
			super();
			this.gate = gate;
			moduleMap = new java.util.HashMap<java.lang.Class<?>, RubyModule>();
			klassMap = new java.util.HashMap<java.lang.Class<?>, RubyClass>();
			interfaceMap = new java.util.HashMap<RubyModule, java.lang.Class<?>>();
			factoryMap = new java.util.HashMap<RubyClass, Factory>();
			return;
		}
		private java.lang.Class<?> __makeInterfaceUnlessJava(RubyModule module) {
			java.lang.Class<?> clazz = Util.getJavaClass(module);
			if (clazz != null)
				return clazz;
			return __makeInterface(module);
		}
		private java.util.ArrayList<java.lang.Class<?>> __makeInterfaces(RubyModule module) {
			java.util.ArrayList<java.lang.Class<?>> list = new java.util.ArrayList<java.lang.Class<?>>();
			RubyClass klass = module.getSuperClass();
			while (true) {
				if (klass == null)
					break;
				if (!klass.isIncluded())
					break;
				java.lang.Class<?> clazz = __makeInterfaceUnlessJava(klass.getNonIncludedClass());
				list.add(clazz);
				klass = klass.getSuperClass();
			}
			return list;
		}
		private java.lang.Class<?> __newInterface(RubyModule module) {
			java.lang.String name = module.getName();
			java.util.ArrayList<java.lang.Class<?>> clazzs = __makeInterfaces(module);
			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
			cw.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC | Opcodes.ACC_ABSTRACT | Opcodes.ACC_INTERFACE, name, null, "java/lang/Object", ASM.interfaces(clazzs));
			cw.visitEnd();
			byte[] data = cw.toByteArray();
			return gate.defineClass(name, data);
		}
		private Factory __makeFactory(RubyClass klass) {
			Factory factory;
			factory = factoryMap.get(klass);
			if (factory == null) {
				factory = __newFactory(klass);
				factoryMap.put(klass, factory);
				klassMap.put(factory.clazz, klass);
			}
			return factory;
		}
		private Factory __newFactory(RubyClass klass) {
			Factory factory = __makeSuperFactory(klass);
			java.util.HashSet<Descriptor> finalSet = factory.finalSet;
			java.lang.Class<?> clazz = factory.clazz;
			java.lang.String superName = clazz.getName().replace('.', '/');
			java.lang.String className = "ruby" + '/' + klass.getName().replace("::", "$");
			java.util.ArrayList<java.lang.Class<?>> clazzs = __makeInterfaces(klass);
			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
			cw.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER, className, null, superName, ASM.interfaces(clazzs));
			ASM.visitConstructors(cw, superName, clazz);
			ASM.visitVirtuals(cw, Util.buildInterfaceVirtualSet(clazzs, finalSet));
			cw.visitEnd();
			byte[] data = cw.toByteArray();
			return new Factory(finalSet, gate.defineClass(className.replace('/', '.'), data));
		}
		private Factory __makeSuperFactory(RubyClass klass) {
			while (true) {
				klass = klass.getSuperClass();
				if (klass == null)
					return Util.fetchFactory(gate.JAVA_LANG_OBJECT_CLASS);
				if (!klass.isIncluded()) {
					IRubyObject box = klass.getInstanceVariable("@__ir_factory");
					if (box == null)
						return __makeFactory(klass);
					Factory factory = (Factory) ((Box) box).getObject();
					if (factory != null)
						return factory;
				}
			}
		}
		private java.lang.Class<?> __makeInterface(RubyModule module) {
			java.lang.Class<?> clazz;
			clazz = interfaceMap.get(module);
			if (clazz == null) {
				clazz = __newInterface(module);
				interfaceMap.put(module, clazz);
				moduleMap.put(clazz, module);
			}
			return clazz;
		}
		private java.lang.Class<?> __makeClass(RubyClass klass) {
			Factory factory = __makeFactory(klass);
			return factory.clazz;
		}
		synchronized RubyModule getModuleOrKlass(java.lang.Class<?> clazz) {
			return clazz.isInterface() ? moduleMap.get(clazz) : klassMap.get(clazz);
		}
		synchronized RubyClass getKlass(java.lang.Class<?> clazz) {
			return klassMap.get(clazz);
		}
		synchronized java.lang.Class<?> makeInterfaceOrClass(RubyModule module) {
			if (module instanceof RubyClass)
				return __makeClass((RubyClass) module);
			else
				return __makeInterface(module);
		}
		synchronized java.lang.Class<?> makeClass(RubyClass klass) {
			return __makeClass(klass);
		}
	}
	static final class Gate {
		static final java.lang.reflect.Method DEFINE_CLASS_METHOD = getDefineClassMethod();
		static java.lang.reflect.Method getDefineClassMethod() {
			java.lang.Class<?>[] parameterTypes = {
				java.lang.String.class,
				byte[].class,
				int.class,
				int.class,
			};
			java.lang.reflect.Method method;
			try {
				method = java.lang.ClassLoader.class.getDeclaredMethod("defineClass", parameterTypes);
			} catch (java.lang.NoSuchMethodException e) {
				e.getCause().printStackTrace();
				return null;
			} catch (java.lang.SecurityException e) {
				e.getCause().printStackTrace();
				return null;
			}
			try {
				method.setAccessible(true);
			} catch (java.lang.SecurityException e) {
				e.getCause().printStackTrace();
				return null;
			}
			return method;
		}
		static final java.util.HashMap<Ruby, Gate> CURRENT = new java.util.HashMap<Ruby, Gate>();
		synchronized static Gate getCurrent(Ruby runtime) {
			return CURRENT.get(runtime);
		}
		synchronized static void setCurrent(Ruby runtime, Gate gate) {
			CURRENT.put(runtime, gate);
			return;
		}
		final Ruby runtime;
		final RubyBignum INTEGER_X8000000000000000;
		final RubyBignum INTEGER_XFFFFFFFFFFFFFFFF;
		final RubyFixnum INTEGER_MX8000000000000000;
		final Int32 INT32_0;
		final Int32 INT32_1;
		final Int32 INT32_M1;
		final RubyModule JAVA_MODULE;
		final RubyModule RUBY_MODULE;
		final RubyClass JAVA_PACKAGE_CLASS;
		final RubyClass JAVA_VALUE_CLASS;
		final RubyClass JAVA_BASIC_OBJECT_CLASS;
		final RubyClass JAVA_ARRAY_CLASS;
		final RubyClass ARRAY_FACTORY_CLASS;
		final RubyClass PRIMITIVE_ARRAY_FACTORY_FACTORY_CLASS;
		final RubyModule VOID_MODULE;
		final RubyClass BYTE_CLASS;
		final RubyClass CHAR_CLASS;
		final RubyClass INT16_CLASS;
		final RubyClass INT32_CLASS;
		final RubyClass INT64_CLASS;
		final RubyClass FLOAT32_CLASS;
		final RubyClass FLOAT64_CLASS;
		final RubyClass BOOLEAN_ARRAY_CLASS;
		final RubyClass BYTE_ARRAY_CLASS;
		final RubyClass CHAR_ARRAY_CLASS;
		final RubyClass INT16_ARRAY_CLASS;
		final RubyClass INT32_ARRAY_CLASS;
		final RubyClass INT64_ARRAY_CLASS;
		final RubyClass FLOAT32_ARRAY_CLASS;
		final RubyClass FLOAT64_ARRAY_CLASS;
		final PrimitiveArrayFactoryFactory BOOLEAN_ARRAY_FACTORY_FACTORY;
		final PrimitiveArrayFactoryFactory BYTE_ARRAY_FACTORY_FACTORY;
		final PrimitiveArrayFactoryFactory CHAR_ARRAY_FACTORY_FACTORY;
		final PrimitiveArrayFactoryFactory INT16_ARRAY_FACTORY_FACTORY;
		final PrimitiveArrayFactoryFactory INT32_ARRAY_FACTORY_FACTORY;
		final PrimitiveArrayFactoryFactory INT64_ARRAY_FACTORY_FACTORY;
		final PrimitiveArrayFactoryFactory FLOAT32_ARRAY_FACTORY_FACTORY;
		final PrimitiveArrayFactoryFactory FLOAT64_ARRAY_FACTORY_FACTORY;
		final java.lang.ClassLoader loader;
		final J2R j2r;
		final R2J r2j;
		final RubyClass JAVA_LANG_OBJECT_CLASS;
		final RubyClass JAVA_LANG_STRING_CLASS;
		final RubyClass JAVA_LANG_CLASS_CLASS;
		Gate(Ruby runtime) {
			super();
			this.runtime = runtime;
			INTEGER_X8000000000000000 = RubyBignum.newBignum(runtime, new java.math.BigInteger("8000000000000000", 0x10));
			INTEGER_XFFFFFFFFFFFFFFFF = RubyBignum.newBignum(runtime, new java.math.BigInteger("FFFFFFFFFFFFFFFF", 0x10));
			INTEGER_MX8000000000000000 = RubyFixnum.newFixnum(runtime, -0x8000000000000000L);
			JAVA_MODULE = runtime.defineModule("JAVA");
			JAVA_MODULE.defineAnnotatedMethods(JAVA.class);
			Util.undefineMethods(JAVA_MODULE.getSingletonClass());
			JAVA_PACKAGE_CLASS = RubyClass.newClass(runtime, runtime.getBasicObject());
			JAVA_PACKAGE_CLASS.defineAnnotatedMethods(JavaPackage.class);
			JAVA_VALUE_CLASS = runtime.defineClassUnder("Value", runtime.getBasicObject(), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR, JAVA_MODULE);
			JAVA_VALUE_CLASS.includeModule(runtime.getKernel());
			JAVA_VALUE_CLASS.defineAnnotatedMethods(JavaValue.class);
			JAVA_BASIC_OBJECT_CLASS = runtime.defineClassUnder("BasicObject", JAVA_VALUE_CLASS, ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR, JAVA_MODULE);
			JAVA_BASIC_OBJECT_CLASS.defineAnnotatedMethods(JavaBasicObject.class);
			JAVA_ARRAY_CLASS = runtime.defineClassUnder("Array", JAVA_VALUE_CLASS, ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR, JAVA_MODULE);
			JAVA_ARRAY_CLASS.defineAnnotatedMethods(JavaArray.class);
			BYTE_CLASS = runtime.defineClass("Byte", JAVA_VALUE_CLASS, ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
			BYTE_CLASS.defineAnnotatedMethods(Byte.class);
			CHAR_CLASS = runtime.defineClass("Char", JAVA_VALUE_CLASS, ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
			CHAR_CLASS.defineAnnotatedMethods(Char.class);
			INT16_CLASS = runtime.defineClass("Int16", JAVA_VALUE_CLASS, ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
			INT16_CLASS.defineAnnotatedMethods(Int16.class);
			INT32_CLASS = runtime.defineClass("Int32", JAVA_VALUE_CLASS, ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
			INT32_CLASS.defineAnnotatedMethods(Int32.class);
			INT64_CLASS = runtime.defineClass("Int64", JAVA_VALUE_CLASS, ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
			INT64_CLASS.defineAnnotatedMethods(Int64.class);
			FLOAT32_CLASS = runtime.defineClass("Float32", JAVA_VALUE_CLASS, ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
			FLOAT32_CLASS.defineAnnotatedMethods(Float32.class);
			FLOAT32_CLASS.defineConstant("NEGATIVE_INFINITY", new Float32(FLOAT32_CLASS, java.lang.Float.NEGATIVE_INFINITY));
			FLOAT32_CLASS.defineConstant("POSITIVE_INFINITY", new Float32(FLOAT32_CLASS, java.lang.Float.POSITIVE_INFINITY));
			FLOAT64_CLASS = runtime.defineClass("Float64", JAVA_VALUE_CLASS, ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
			FLOAT64_CLASS.defineAnnotatedMethods(Float64.class);
			FLOAT64_CLASS.defineConstant("NEGATIVE_INFINITY", new Float64(FLOAT64_CLASS, java.lang.Double.NEGATIVE_INFINITY));
			FLOAT64_CLASS.defineConstant("POSITIVE_INFINITY", new Float64(FLOAT64_CLASS, java.lang.Double.POSITIVE_INFINITY));
			INT32_0 = new Int32(INT32_CLASS, 0);
			INT32_1 = new Int32(INT32_CLASS, 1);
			INT32_M1 = new Int32(INT32_CLASS, -1);
			ARRAY_FACTORY_CLASS = RubyClass.newClass(runtime, runtime.getObject());
			ARRAY_FACTORY_CLASS.defineAnnotatedMethods(ArrayFactory.class);
			PRIMITIVE_ARRAY_FACTORY_FACTORY_CLASS = RubyClass.newClass(runtime, runtime.getObject());
			PRIMITIVE_ARRAY_FACTORY_FACTORY_CLASS.defineAnnotatedMethods(PrimitiveArrayFactoryFactory.class);
			VOID_MODULE = RubyModule.newModule(runtime);
			VOID_MODULE.defineAnnotatedMethods(VOID.class);
			BOOLEAN_ARRAY_CLASS = RubyClass.newClass(runtime, JAVA_ARRAY_CLASS);
			BOOLEAN_ARRAY_CLASS.setBaseName("boolean[]");
			BOOLEAN_ARRAY_CLASS.setAllocator(ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
			BOOLEAN_ARRAY_CLASS.defineAnnotatedMethods(BooleanArray.class);
			Util.setJavaClass(BOOLEAN_ARRAY_CLASS, boolean[].class);
			BOOLEAN_ARRAY_FACTORY_FACTORY = new PrimitiveArrayFactoryFactory(PRIMITIVE_ARRAY_FACTORY_FACTORY_CLASS, BOOLEAN_ARRAY_CLASS);
			BYTE_ARRAY_CLASS = RubyClass.newClass(runtime, JAVA_ARRAY_CLASS);
			BYTE_ARRAY_CLASS.setBaseName("byte[]");
			BYTE_ARRAY_CLASS.setAllocator(ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
			BYTE_ARRAY_CLASS.defineAnnotatedMethods(ByteArray.class);
			Util.setJavaClass(BYTE_ARRAY_CLASS, byte[].class);
			BYTE_ARRAY_FACTORY_FACTORY = new PrimitiveArrayFactoryFactory(PRIMITIVE_ARRAY_FACTORY_FACTORY_CLASS, BYTE_ARRAY_CLASS);
			CHAR_ARRAY_CLASS = RubyClass.newClass(runtime, JAVA_ARRAY_CLASS);
			CHAR_ARRAY_CLASS.setBaseName("char[]");
			CHAR_ARRAY_CLASS.setAllocator(ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
			CHAR_ARRAY_CLASS.defineAnnotatedMethods(CharArray.class);
			Util.setJavaClass(CHAR_ARRAY_CLASS, char[].class);
			CHAR_ARRAY_FACTORY_FACTORY = new PrimitiveArrayFactoryFactory(PRIMITIVE_ARRAY_FACTORY_FACTORY_CLASS, CHAR_ARRAY_CLASS);
			INT16_ARRAY_CLASS = RubyClass.newClass(runtime, JAVA_ARRAY_CLASS);
			INT16_ARRAY_CLASS.setBaseName("int16[]");
			INT16_ARRAY_CLASS.setAllocator(ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
			INT16_ARRAY_CLASS.defineAnnotatedMethods(Int16Array.class);
			Util.setJavaClass(INT16_ARRAY_CLASS, short[].class);
			INT16_ARRAY_FACTORY_FACTORY = new PrimitiveArrayFactoryFactory(PRIMITIVE_ARRAY_FACTORY_FACTORY_CLASS, INT16_ARRAY_CLASS);
			INT32_ARRAY_CLASS = RubyClass.newClass(runtime, JAVA_ARRAY_CLASS);
			INT32_ARRAY_CLASS.setBaseName("int32[]");
			INT32_ARRAY_CLASS.setAllocator(ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
			INT32_ARRAY_CLASS.defineAnnotatedMethods(Int32Array.class);
			Util.setJavaClass(INT32_ARRAY_CLASS, int[].class);
			INT32_ARRAY_FACTORY_FACTORY = new PrimitiveArrayFactoryFactory(PRIMITIVE_ARRAY_FACTORY_FACTORY_CLASS, INT32_ARRAY_CLASS);
			INT64_ARRAY_CLASS = RubyClass.newClass(runtime, JAVA_ARRAY_CLASS);
			INT64_ARRAY_CLASS.setBaseName("int64[]");
			INT64_ARRAY_CLASS.setAllocator(ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
			INT64_ARRAY_CLASS.defineAnnotatedMethods(Int64Array.class);
			Util.setJavaClass(INT64_ARRAY_CLASS, long[].class);
			INT64_ARRAY_FACTORY_FACTORY = new PrimitiveArrayFactoryFactory(PRIMITIVE_ARRAY_FACTORY_FACTORY_CLASS, INT64_ARRAY_CLASS);
			FLOAT32_ARRAY_CLASS = RubyClass.newClass(runtime, JAVA_ARRAY_CLASS);
			FLOAT32_ARRAY_CLASS.setBaseName("float32[]");
			FLOAT32_ARRAY_CLASS.setAllocator(ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
			FLOAT32_ARRAY_CLASS.defineAnnotatedMethods(Float32Array.class);
			Util.setJavaClass(FLOAT32_ARRAY_CLASS, float[].class);
			FLOAT32_ARRAY_FACTORY_FACTORY = new PrimitiveArrayFactoryFactory(PRIMITIVE_ARRAY_FACTORY_FACTORY_CLASS, FLOAT32_ARRAY_CLASS);
			FLOAT64_ARRAY_CLASS = RubyClass.newClass(runtime, JAVA_ARRAY_CLASS);
			FLOAT64_ARRAY_CLASS.setBaseName("float64[]");
			FLOAT64_ARRAY_CLASS.setAllocator(ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
			FLOAT64_ARRAY_CLASS.defineAnnotatedMethods(Float64Array.class);
			Util.setJavaClass(FLOAT64_ARRAY_CLASS, double[].class);
			FLOAT64_ARRAY_FACTORY_FACTORY = new PrimitiveArrayFactoryFactory(PRIMITIVE_ARRAY_FACTORY_FACTORY_CLASS, FLOAT64_ARRAY_CLASS);
			RUBY_MODULE = RubyModule.newModule(runtime);
			RUBY_MODULE.defineAnnotatedMethods(RUBY.class);
			loader = runtime.getJRubyClassLoader();
			j2r = new J2R(this);
			r2j = new R2J(this);
			JAVA_LANG_OBJECT_CLASS = j2r.makeKlass(java.lang.Object.class);
			JAVA_LANG_STRING_CLASS = j2r.makeKlass(java.lang.String.class);
			JAVA_LANG_CLASS_CLASS = j2r.makeKlass(java.lang.Class.class);
			return;
		}
		java.lang.Class<?> defineClass(java.lang.String name, byte[] data) {
			java.lang.Object[] args = { name, data, 0, data.length };
			java.lang.Object j;
			try {
				j = DEFINE_CLASS_METHOD.invoke(loader, args);
			} catch (java.lang.IllegalAccessException e) {
				return null;
			} catch (java.lang.reflect.InvocationTargetException e) {
				e.getCause().printStackTrace();
				return null;
			}
			return (java.lang.Class) j;
		}
		Factory newFactory(java.lang.Class<?> clazz) {
			java.util.HashSet<Descriptor> finalSet = Util.buildFinalSet(clazz);
			java.lang.String superName = clazz.getName().replace('.', '/');
			java.lang.String className = "__ir" + '/' + superName;
			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
			cw.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC | Opcodes.ACC_ABSTRACT | Opcodes.ACC_SUPER, className, null, superName, new java.lang.String[] { Adapter.NAME });
			ASM.visitObjectField(cw);
			ASM.visitObjectReader(cw, className);
			ASM.visitObjectWriter(cw, className);
			ASM.visitConstructors(cw, superName, clazz);
			ASM.visitSupers(cw, superName, Util.buildSuperSet(clazz));
			ASM.visitVirtuals(cw, Util.buildClassVirtualSet(clazz, finalSet));
			cw.visitEnd();
			byte[] data = cw.toByteArray();
			return new Factory(finalSet, defineClass(className.replace('/', '.'), data));
		}
		RubyModule makeModuleOrKlass(java.lang.Class<?> clazz) {
			RubyModule module = r2j.getModuleOrKlass(clazz);
			if (module != null)
				return module;
			return j2r.makeModuleOrKlass(clazz);
		}
		RubyClass makeArrayKlass(java.lang.Class<?> clazz) {
			if (clazz.isPrimitive()) {
				if (clazz == boolean.class)
					return BOOLEAN_ARRAY_CLASS;
				if (clazz == byte.class)
					return BYTE_ARRAY_CLASS;
				if (clazz == char.class)
					return CHAR_ARRAY_CLASS;
				if (clazz == short.class)
					return INT16_ARRAY_CLASS;
				if (clazz == int.class)
					return INT32_ARRAY_CLASS;
				if (clazz == long.class)
					return INT64_ARRAY_CLASS;
				if (clazz == float.class)
					return FLOAT32_ARRAY_CLASS;
				if (clazz == double.class)
					return FLOAT64_ARRAY_CLASS;
				throw new java.lang.AssertionError();
			}
			if (clazz.isArray())
				return Util.makeOuter(runtime, makeArrayKlass(clazz.getComponentType()));
			return Util.makeOuter(runtime, makeModuleOrKlass(clazz));
		}
		ObjectArray newArrayArrayProxy(java.lang.Class<?> clazz, java.lang.Object j) {
			RubyClass klass = Util.makeOuter(runtime, makeArrayKlass(clazz));
			return new ObjectArray(klass, (java.lang.Object[]) j);
		}
		ObjectArray newObjectArrayProxy(java.lang.Class<?> clazz, java.lang.Object j) {
			RubyClass klass = Util.makeOuter(runtime, makeModuleOrKlass(clazz));
			return new ObjectArray(klass, (java.lang.Object[]) j);
		}
		JavaArray newArrayProxy(java.lang.Class<?> clazz, java.lang.Object j) {
			if (clazz.isPrimitive()) {
				if (clazz == boolean.class)
					return new BooleanArray(BOOLEAN_ARRAY_CLASS, (boolean[]) j);
				if (clazz == byte.class)
					return new ByteArray(BYTE_ARRAY_CLASS, (byte[]) j);
				if (clazz == char.class)
					return new CharArray(CHAR_ARRAY_CLASS, (char[]) j);
				if (clazz == short.class)
					return new Int16Array(INT16_ARRAY_CLASS, (short[]) j);
				if (clazz == int.class)
					return new Int32Array(INT32_ARRAY_CLASS, (int[]) j);
				if (clazz == long.class)
					return new Int64Array(INT64_ARRAY_CLASS, (long[]) j);
				if (clazz == float.class)
					return new Float32Array(FLOAT32_ARRAY_CLASS, (float[]) j);
				if (clazz == double.class)
					return new Float64Array(FLOAT64_ARRAY_CLASS, (double[]) j);
				throw new java.lang.AssertionError();
			}
			if (clazz.isArray())
				return newArrayArrayProxy(clazz.getComponentType(), j);
			return newObjectArrayProxy(clazz, j);
		}
		JavaObject newJavaObject(java.lang.Class<?> clazz, java.lang.Object j) {
			return new JavaObject(j2r.makeKlass(clazz), j);
		}
		JavaObject newStringProxy(java.lang.String j) {
			return new JavaObject(JAVA_LANG_STRING_CLASS, j);
		}
		JavaObject newClassProxy(java.lang.Class<?> j) {
			return new JavaObject(JAVA_LANG_CLASS_CLASS, j);
		}
		Proxy newProxy(java.lang.Object j) {
			java.lang.Class<?> clazz = j.getClass();
			if (clazz.isArray())
				return newArrayProxy(clazz.getComponentType(), j);
			return newJavaObject(clazz, j);
		}
	}
	static final class ErrorInfoGlobalVariable extends org.jruby.runtime.ReadonlyGlobalVariable {
		ErrorInfoGlobalVariable(Ruby runtime, String name, IRubyObject value) {
			super(runtime, name, value);
			return;
		}
		@Override
		public IRubyObject get() {
			IRubyObject e = runtime.getCurrentContext().getErrorInfo();
			if (e instanceof ConcreteJavaProxy)
				return Util.makeProxyUnlessAdapter(runtime, ((ConcreteJavaProxy) e).getObject());
			return e;
		}
	}
	static final class WeakValueIdentityHashMap<K, V> {
		static final class WeakReferenceWithKey<V, K> extends java.lang.ref.WeakReference<V> {
			final K k;
			WeakReferenceWithKey(V v, java.lang.ref.ReferenceQueue<? super V> queue, K k) {
				super(v, queue);
				this.k = k;
				return;
			}
		}
		final java.util.IdentityHashMap<K, WeakReferenceWithKey<V, K>> map;
		final java.lang.ref.ReferenceQueue<V> queue;
		WeakValueIdentityHashMap() {
			super();
			map = new java.util.IdentityHashMap<K, WeakReferenceWithKey<V, K>>();
			queue = new java.lang.ref.ReferenceQueue<V>();
			return;
		}
		V get(K k) {
			expunge();
			WeakReferenceWithKey<V, K> ref = map.get(k);
			if (ref == null)
				return null;
			return ref.get();
		}
		void put(K k, V v) {
			expunge();
			map.put(k, new WeakReferenceWithKey<V, K>(v, queue, k));
			return;
		}
		void expunge() {
			WeakReferenceWithKey<V, K> ref;
			while (true) {
				ref = (WeakReferenceWithKey<V, K>) queue.poll();
				if (ref == null)
					break;
				map.remove(ref.k);
			}
			return;
		}
	}
	static final class Util {
		static void undefineMethods(RubyClass klass) {
			klass.undefineMethod("java");
			klass.undefineMethod("com");
			klass.undefineMethod("org");
			return;
		}
		static RubyClass getRealClass(IRubyObject o) {
			return o.getMetaClass().getRealClass();
		}
		static java.lang.Object newInstance(java.lang.Class<?> clazz) {
			try {
				return clazz.newInstance();
			} catch (java.lang.IllegalAccessException e) {
				Helpers.throwException(e);
				throw new AssertionError();
			} catch (java.lang.InstantiationException e) {
				Helpers.throwException(e);
				throw new AssertionError();
			}
		}
		static java.lang.Object newInstance(java.lang.reflect.Constructor<?> constructor, java.lang.Object[] a) {
			try {
				return constructor.newInstance(a);
			} catch (java.lang.IllegalAccessException e) {
				Helpers.throwException(e);
				throw new AssertionError();
			} catch (java.lang.InstantiationException e) {
				Helpers.throwException(e);
				throw new AssertionError();
			} catch (java.lang.reflect.InvocationTargetException e) {
				Helpers.throwException(e.getCause());
				throw new AssertionError();
			}
		}
		static java.lang.Object invoke(java.lang.Object o, java.lang.reflect.Method method, java.lang.Object[] a) {
			try {
				return method.invoke(o, a);
			} catch (java.lang.IllegalAccessException e) {
				Helpers.throwException(e);
				throw new AssertionError();
			} catch (java.lang.reflect.InvocationTargetException e) {
				Helpers.throwException(e.getCause());
				throw new AssertionError();
			}
		}
		static boolean isAbstract(java.lang.Class clazz) {
			return (clazz.getModifiers() & java.lang.reflect.Modifier.ABSTRACT) != 0;
		}
		static boolean isFinal(java.lang.Class<?> clazz) {
			return (clazz.getModifiers() & java.lang.reflect.Modifier.FINAL) != 0;
		}
		static boolean isPublic(java.lang.Class<?> clazz) {
			return (clazz.getModifiers() & java.lang.reflect.Modifier.PUBLIC) != 0;
		}
		static boolean isFinal(java.lang.reflect.Field field) {
			return (field.getModifiers() & java.lang.reflect.Modifier.FINAL) != 0;
		}
		static boolean isPublic(java.lang.reflect.Field field) {
			return (field.getModifiers() & java.lang.reflect.Modifier.PUBLIC) != 0;
		}
		static boolean isStatic(java.lang.reflect.Field field) {
			return (field.getModifiers() & java.lang.reflect.Modifier.STATIC) != 0;
		}
		static boolean isPublic(java.lang.reflect.Constructor<?> constructor) {
			return (constructor.getModifiers() & java.lang.reflect.Modifier.PUBLIC) != 0;
		}
		static boolean isProtected(java.lang.reflect.Constructor<?> constructor) {
			return (constructor.getModifiers() & java.lang.reflect.Modifier.PROTECTED) != 0;
		}
		static boolean isAbstract(java.lang.reflect.Method method) {
			return (method.getModifiers() & java.lang.reflect.Modifier.ABSTRACT) != 0;
		}
		static boolean isFinal(java.lang.reflect.Method method) {
			return (method.getModifiers() & java.lang.reflect.Modifier.FINAL) != 0;
		}
		static boolean isPublic(java.lang.reflect.Method method) {
			return (method.getModifiers() & java.lang.reflect.Modifier.PUBLIC) != 0;
		}
		static boolean isProtected(java.lang.reflect.Method method) {
			return (method.getModifiers() & java.lang.reflect.Modifier.PROTECTED) != 0;
		}
		static boolean isStatic(java.lang.reflect.Method method) {
			return (method.getModifiers() & java.lang.reflect.Modifier.STATIC) != 0;
		}
		static java.lang.Class<?> getJavaClass(RubyModule module) {
			IRubyObject box = module.getInstanceVariable("@__ir_java_class");
			if (box != null)
				return (java.lang.Class<?>) ((Box) box).getObject();
			return null;
		}
		static java.lang.Class<?> fetchJavaClass(RubyModule module) {
			IRubyObject box = module.getInstanceVariable("@__ir_java_class");
			if (box != null)
				return (java.lang.Class<?>) ((Box) box).getObject();
			throw new AssertionError();
		}
		static void setJavaClass(RubyModule module, java.lang.Class<?> clazz) {
			Ruby runtime = module.getRuntime();
			module.setInstanceVariable("@__ir_java_class", new Box(runtime.getObject(), clazz));
			return;
		}
		static Factory fetchFactory(RubyModule module) {
			IRubyObject box = module.getInstanceVariable("@__ir_factory");
			if (box != null)
				return (Factory) ((Box) box).getObject();
			throw new AssertionError();
		}
		static void setFactory(RubyModule module, Factory factory) {
			Ruby runtime = module.getRuntime();
			module.setInstanceVariable("@__ir_factory", new Box(runtime.getObject(), factory));
			return;
		}
		static java.util.HashSet<Descriptor> buildFinalSet(java.lang.Class<?> clazz) {
			java.util.HashSet<Descriptor> finalSet = new java.util.HashSet<Descriptor>();
			while (true) {
				java.lang.reflect.Method[] methods = clazz.getDeclaredMethods();
				for (java.lang.reflect.Method method : methods)
					if (!isStatic(method))
						if (isFinal(method))
							finalSet.add(new Descriptor(method));
				clazz = clazz.getSuperclass();
				if (clazz == null)
					break;
			}
			return finalSet;
		}
		static java.util.HashSet<Descriptor> buildSuperSet(java.lang.Class<?> clazz) {
			java.util.HashSet<Descriptor> superSet = new java.util.HashSet<Descriptor>();
			while (true) {
				java.lang.reflect.Method[] methods = clazz.getDeclaredMethods();
				for (java.lang.reflect.Method method : methods)
					if (!isStatic(method))
						if (!isAbstract(method))
							if (isPublic(method) || isProtected(method))
								superSet.add(new Descriptor(method));
				clazz = clazz.getSuperclass();
				if (clazz == null)
					break;
			}
			return superSet;
		}
		static void scanMethod(java.lang.reflect.Method method, java.util.HashSet<Descriptor> finalSet, java.util.HashSet<Descriptor> virtualSet) {
			Descriptor d = new Descriptor(method);
			if (!finalSet.contains(d))
				virtualSet.add(d);
			return;
		}
		static void scanInterfaceMethods(java.lang.Class<?> clazz, java.util.HashSet<Descriptor> finalSet, java.util.HashSet<Descriptor> virtualSet) {
			java.lang.reflect.Method[] methods = clazz.getDeclaredMethods();
			for (java.lang.reflect.Method method : methods)
				scanMethod(method, finalSet, virtualSet);
			return;
		}
		static void scanAllInterfaceMethods(java.lang.Class<?> clazz, java.util.HashSet<Descriptor> finalSet, java.util.HashSet<Descriptor> virtualSet) {
			scanAllInterfaceMethods(clazz.getInterfaces(), finalSet, virtualSet);
			scanInterfaceMethods(clazz, finalSet, virtualSet);
			return;
		}
		static void scanAllInterfaceMethods(java.lang.Class<?>[] clazzs, java.util.HashSet<Descriptor> finalSet, java.util.HashSet<Descriptor> virtualSet) {
			for (java.lang.Class<?> clazz : clazzs)
				scanAllInterfaceMethods(clazz, finalSet, virtualSet);
			return;
		}
		static void scanVirtualMethods(java.lang.Class<?> clazz, java.util.HashSet<Descriptor> finalSet, java.util.HashSet<Descriptor> virtualSet) {
			java.lang.reflect.Method[] methods = clazz.getDeclaredMethods();
			for (java.lang.reflect.Method method : methods)
				if (!isStatic(method))
					if (isPublic(method) || isProtected(method))
						scanMethod(method, finalSet, virtualSet);
			return;
		}
		static void scanAllVirtualMethods(java.lang.Class<?> clazz, java.util.HashSet<Descriptor> finalSet, java.util.HashSet<Descriptor> virtualSet) {
			while (true) {
				scanAllInterfaceMethods(clazz.getInterfaces(), finalSet, virtualSet);
				scanVirtualMethods(clazz, finalSet, virtualSet);
				clazz = clazz.getSuperclass();
				if (clazz == null)
					break;
			}
			return;
		}
		static java.util.HashSet<Descriptor> buildInterfaceVirtualSet(java.util.ArrayList<java.lang.Class<?>> clazzs, java.util.HashSet<Descriptor> finalSet) {
			java.util.HashSet<Descriptor> virtualSet = new java.util.HashSet<Descriptor>();
			for (java.lang.Class<?> clazz : clazzs)
				scanAllInterfaceMethods(clazz, finalSet, virtualSet);
			return virtualSet;
		}
		static java.util.HashSet<Descriptor> buildClassVirtualSet(java.lang.Class<?> clazz, java.util.HashSet<Descriptor> finalSet) {
			java.util.HashSet<Descriptor> virtualSet = new java.util.HashSet<Descriptor>();
			scanAllVirtualMethods(clazz, finalSet, virtualSet);
			return virtualSet;
		}
		private static Adapter __newAdapter(Ruby runtime, RubyBasicObject r) {
			RubyClass klass = r.getMetaClass();
			java.lang.Class<?> clazz = Gate.getCurrent(runtime).r2j.makeClass(klass);
			Adapter adapter = (Adapter) newInstance(clazz);
			adapter.__ir_set$object(r);
			return adapter;
		}
		static Adapter makeAdapter(Ruby runtime, RubyBasicObject r) {
			Adapter adapter;
			synchronized(Adapter.CACHE) {
				adapter = Adapter.CACHE.get(r);
				if (adapter == null) {
					adapter = __newAdapter(runtime, r);
					Adapter.CACHE.put(r, adapter);
				}
			}
			return adapter;
		}
		static java.lang.Object makeAdapterUnlessProxy(Ruby runtime, IRubyObject r) {
			if (r instanceof Proxy) {
				Proxy proxy = (Proxy) r;
				java.lang.Object j = proxy.getObject();
				synchronized(Proxy.CACHE) {
					Proxy.CACHE.put(j, proxy);
				}
				return j;
			}
			return makeAdapter(runtime, (RubyBasicObject) r);
		}
		static java.lang.Object makeAdapterUnlessProxyMaybe(Ruby runtime, IRubyObject r) {
			if (r instanceof RubyNil)
				return null;
			return makeAdapterUnlessProxy(runtime, r);
		}
		static RubyBoolean newBoolean(Ruby runtime, boolean value) {
			return RubyBoolean.newBoolean(runtime, value);
		}
		static Byte newByte(Ruby runtime, byte value) {
			return new Byte(Gate.getCurrent(runtime).BYTE_CLASS, value);
		}
		static Char newChar(Ruby runtime, char value) {
			return new Char(Gate.getCurrent(runtime).CHAR_CLASS, value);
		}
		static Int16 newInt16(Ruby runtime, short value) {
			return new Int16(Gate.getCurrent(runtime).INT16_CLASS, value);
		}
		static Int32 newInt32(Ruby runtime, int value) {
			return new Int32(Gate.getCurrent(runtime).INT32_CLASS, value);
		}
		static Int64 newInt64(Ruby runtime, long value) {
			return new Int64(Gate.getCurrent(runtime).INT64_CLASS, value);
		}
		static Float32 newFloat32(Ruby runtime, float value) {
			return new Float32(Gate.getCurrent(runtime).FLOAT32_CLASS, value);
		}
		static Float64 newFloat64(Ruby runtime, double value) {
			return new Float64(Gate.getCurrent(runtime).FLOAT64_CLASS, value);
		}
		private static Proxy __newProxy(Ruby runtime, java.lang.Object j) {
			return Gate.getCurrent(runtime).newProxy(j);
		}
		static Proxy makeProxy(Ruby runtime, java.lang.Object j) {
			Proxy proxy;
			synchronized(Proxy.CACHE) {
				proxy = Proxy.CACHE.get(j);
				if (proxy == null) {
					proxy = __newProxy(runtime, j);
					Proxy.CACHE.put(j, proxy);
				}
			}
			return proxy;
		}
		static IRubyObject makeProxyUnlessAdapter(Ruby runtime, java.lang.Object j) {
			if (j instanceof Adapter)
				return ((Adapter) j).__ir_get$object();
			return makeProxy(runtime, j);
		}
		static IRubyObject makeProxyUnlessAdapterMaybe(Ruby runtime, java.lang.Object j) {
			if (j == null)
				return runtime.getNil();
			return makeProxyUnlessAdapter(runtime, j);
		}
		static IRubyObject makeValue(Ruby runtime, java.lang.Class<?> type, java.lang.Object j) {
			if (type.isPrimitive()) {
				if (type == boolean.class)
					return newBoolean(runtime, (java.lang.Boolean) j);
				if (type == byte.class)
					return newByte(runtime, (java.lang.Byte) j);
				if (type == char.class)
					return newChar(runtime, (java.lang.Character) j);
				if (type == short.class)
					return newInt16(runtime, (java.lang.Short) j);
				if (type == int.class)
					return newInt32(runtime, (java.lang.Integer) j);
				if (type == long.class)
					return newInt64(runtime, (java.lang.Long) j);
				if (type == float.class)
					return newFloat32(runtime, (java.lang.Float) j);
				if (type == double.class)
					return newFloat64(runtime, (java.lang.Double) j);
				throw new java.lang.AssertionError();
			}
			return makeProxyUnlessAdapter(runtime, j);
		}
		static IRubyObject makeValueMaybe(Ruby runtime, java.lang.Class<?> type, java.lang.Object j) {
			if (j == null)
				return runtime.getNil();
			return makeValue(runtime, type, j);
		}
		static IRubyObject getFieldBoolean(Ruby runtime, java.lang.Object o, java.lang.reflect.Field field) {
			boolean j;
			try {
				j = field.getBoolean(o);
			} catch (java.lang.IllegalAccessException e) {
				Helpers.throwException(e);
				throw new AssertionError();
			}
			return newBoolean(runtime, j);
		}
		static IRubyObject getFieldByte(Ruby runtime, java.lang.Object o, java.lang.reflect.Field field) {
			byte j;
			try {
				j = field.getByte(o);
			} catch (java.lang.IllegalAccessException e) {
				Helpers.throwException(e);
				throw new AssertionError();
			}
			return newByte(runtime, j);
		}
		static IRubyObject getFieldChar(Ruby runtime, java.lang.Object o, java.lang.reflect.Field field) {
			char j;
			try {
				j = field.getChar(o);
			} catch (java.lang.IllegalAccessException e) {
				Helpers.throwException(e);
				throw new AssertionError();
			}
			return newChar(runtime, j);
		}
		static IRubyObject getFieldInt16(Ruby runtime, java.lang.Object o, java.lang.reflect.Field field) {
			short j;
			try {
				j = field.getShort(o);
			} catch (java.lang.IllegalAccessException e) {
				Helpers.throwException(e);
				throw new AssertionError();
			}
			return newInt16(runtime, j);
		}
		static IRubyObject getFieldInt32(Ruby runtime, java.lang.Object o, java.lang.reflect.Field field) {
			int j;
			try {
				j = field.getInt(o);
			} catch (java.lang.IllegalAccessException e) {
				Helpers.throwException(e);
				throw new AssertionError();
			}
			return newInt32(runtime, j);
		}
		static IRubyObject getFieldInt64(Ruby runtime, java.lang.Object o, java.lang.reflect.Field field) {
			long j;
			try {
				j = field.getLong(o);
			} catch (java.lang.IllegalAccessException e) {
				Helpers.throwException(e);
				throw new AssertionError();
			}
			return newInt64(runtime, j);
		}
		static IRubyObject getFieldFloat32(Ruby runtime, java.lang.Object o, java.lang.reflect.Field field) {
			float j;
			try {
				j = field.getFloat(o);
			} catch (java.lang.IllegalAccessException e) {
				Helpers.throwException(e);
				throw new AssertionError();
			}
			return newFloat32(runtime, j);
		}
		static IRubyObject getFieldFloat64(Ruby runtime, java.lang.Object o, java.lang.reflect.Field field) {
			double j;
			try {
				j = field.getDouble(o);
			} catch (java.lang.IllegalAccessException e) {
				Helpers.throwException(e);
				throw new AssertionError();
			}
			return newFloat64(runtime, j);
		}
		static IRubyObject getField(Ruby runtime, java.lang.Object o, java.lang.reflect.Field field) {
			java.lang.Class<?> type = field.getType();
			if (type.isPrimitive()) {
				if (type == boolean.class)
					return getFieldBoolean(runtime, o, field);
				if (type == byte.class)
					return getFieldByte(runtime, o, field);
				if (type == char.class)
					return getFieldChar(runtime, o, field);
				if (type == short.class)
					return getFieldInt16(runtime, o, field);
				if (type == int.class)
					return getFieldInt32(runtime, o, field);
				if (type == long.class)
					return getFieldInt64(runtime, o, field);
				if (type == float.class)
					return getFieldFloat32(runtime, o, field);
				if (type == double.class)
					return getFieldFloat64(runtime, o, field);
				throw new java.lang.AssertionError();
			}
			java.lang.Object j;
			try {
				j = field.get(o);
			} catch (java.lang.IllegalAccessException e) {
				Helpers.throwException(e);
				throw new AssertionError();
			}
			return makeProxyUnlessAdapterMaybe(runtime, j);
		}
		static RubyNil setFieldBoolean(Ruby runtime, java.lang.Object o, java.lang.reflect.Field field, IRubyObject r) {
			boolean j = acceptBoolean(runtime, r);
			try {
				field.setBoolean(o, j);
			} catch (java.lang.IllegalAccessException e) {
				Helpers.throwException(e);
				throw new AssertionError();
			}
			return (RubyNil) runtime.getNil();
		}
		static RubyNil setFieldByte(Ruby runtime, java.lang.Object o, java.lang.reflect.Field field, IRubyObject r) {
			byte j = acceptByte(runtime, r);
			try {
				field.setByte(o, j);
			} catch (java.lang.IllegalAccessException e) {
				Helpers.throwException(e);
				throw new AssertionError();
			}
			return (RubyNil) runtime.getNil();
		}
		static RubyNil setFieldChar(Ruby runtime, java.lang.Object o, java.lang.reflect.Field field, IRubyObject r) {
			char j = acceptChar(runtime, r);
			try {
				field.setChar(o, j);
			} catch (java.lang.IllegalAccessException e) {
				Helpers.throwException(e);
				throw new AssertionError();
			}
			return (RubyNil) runtime.getNil();
		}
		static RubyNil setFieldInt16(Ruby runtime, java.lang.Object o, java.lang.reflect.Field field, IRubyObject r) {
			short j = acceptInt16(runtime, r);
			try {
				field.setShort(o, j);
			} catch (java.lang.IllegalAccessException e) {
				Helpers.throwException(e);
				throw new AssertionError();
			}
			return (RubyNil) runtime.getNil();
		}
		static RubyNil setFieldInt32(Ruby runtime, java.lang.Object o, java.lang.reflect.Field field, IRubyObject r) {
			int j = acceptInt32(runtime, r);
			try {
				field.setInt(o, j);
			} catch (java.lang.IllegalAccessException e) {
				Helpers.throwException(e);
				throw new AssertionError();
			}
			return (RubyNil) runtime.getNil();
		}
		static RubyNil setFieldInt64(Ruby runtime, java.lang.Object o, java.lang.reflect.Field field, IRubyObject r) {
			long j = acceptInt64(runtime, r);
			try {
				field.setLong(o, j);
			} catch (java.lang.IllegalAccessException e) {
				Helpers.throwException(e);
				throw new AssertionError();
			}
			return (RubyNil) runtime.getNil();
		}
		static RubyNil setFieldFloat32(Ruby runtime, java.lang.Object o, java.lang.reflect.Field field, IRubyObject r) {
			float j = acceptFloat32(runtime, r);
			try {
				field.setFloat(o, j);
			} catch (java.lang.IllegalAccessException e) {
				Helpers.throwException(e);
				throw new AssertionError();
			}
			return (RubyNil) runtime.getNil();
		}
		static RubyNil setFieldFloat64(Ruby runtime, java.lang.Object o, java.lang.reflect.Field field, IRubyObject r) {
			double j = acceptFloat64(runtime, r);
			try {
				field.setDouble(o, j);
			} catch (java.lang.IllegalAccessException e) {
				Helpers.throwException(e);
				throw new AssertionError();
			}
			return (RubyNil) runtime.getNil();
		}
		static RubyNil setField(Ruby runtime, java.lang.Object o, java.lang.reflect.Field field, IRubyObject r) {
			java.lang.Class<?> type = field.getType();
			if (type.isPrimitive()) {
				if (type == boolean.class)
					return setFieldBoolean(runtime, o, field, r);
				if (type == byte.class)
					return setFieldByte(runtime, o, field, r);
				if (type == char.class)
					return setFieldChar(runtime, o, field, r);
				if (type == short.class)
					return setFieldInt16(runtime, o, field, r);
				if (type == int.class)
					return setFieldInt32(runtime, o, field, r);
				if (type == long.class)
					return setFieldInt64(runtime, o, field, r);
				if (type == float.class)
					return setFieldFloat32(runtime, o, field, r);
				if (type == double.class)
					return setFieldFloat64(runtime, o, field, r);
				throw new java.lang.AssertionError();
			}
			java.lang.Object j = makeAdapterUnlessProxyMaybe(runtime, r);
			try {
				field.set(o, j);
			} catch (java.lang.IllegalAccessException e) {
				Helpers.throwException(e);
				throw new AssertionError();
			}
			return (RubyNil) runtime.getNil();
		}
		static java.lang.Object convertArg(Ruby runtime, java.lang.Class<?> type, IRubyObject r) {
			if (type.isPrimitive()) {
				if (type == boolean.class) {
					if (r instanceof RubyBoolean.True)
						return true;
					if (r instanceof RubyBoolean.False)
						return false;
					return null;
				}
				if (type == byte.class) {
					if (r instanceof Byte)
						return ((Byte) r).value;
					return null;
				}
				if (type == char.class) {
					if (r instanceof Char)
						return ((Char) r).value;
					return null;
				}
				if (type == short.class) {
					if (r instanceof Int16)
						return ((Int16) r).value;
					return null;
				}
				if (type == int.class) {
					if (r instanceof Int32)
						return ((Int32) r).value;
					return null;
				}
				if (type == long.class) {
					if (r instanceof Int64)
						return ((Int64) r).value;
					return null;
				}
				if (type == float.class) {
					if (r instanceof Float32)
						return ((Float32) r).value;
					return null;
				}
				if (type == double.class) {
					if (r instanceof Float64)
						return ((Float64) r).value;
					return null;
				}
				throw new java.lang.AssertionError();
			}
			java.lang.Object o = makeAdapterUnlessProxy(runtime, r);
			if (type.isInstance(o))
				return o;
			return null;
		}
		static java.lang.Object[] convertArgs(Ruby runtime, java.lang.Class<?>[] types, IRubyObject[] args) {
			int n = types.length;
			if (args.length != n)
				return null;
			java.lang.Object[] a = new java.lang.Object[n];
			for (int i = 0; i < n; i += 1) {
				IRubyObject r = args[i];
				java.lang.Class<?> type = types[i];
				if (r instanceof RubyNil) {
					if (type.isPrimitive())
						return null;
					continue;
				}
				java.lang.Object j = convertArg(runtime, type, r);
				if (j == null)
					return null;
				a[i] = j;
			}
			return a;
		}
		static java.lang.Object fitConstructor(Ruby runtime, java.lang.Class<?> clazz, IRubyObject[] args) {
			java.lang.reflect.Constructor<?>[] constructors = clazz.getConstructors();
			for (java.lang.reflect.Constructor<?> constructor : constructors) {
				if (!isPublic(constructor))
					continue;
				java.lang.Object[] a = convertArgs(runtime, constructor.getParameterTypes(), args);
				if (a != null)
					return newInstance(constructor, a);
			}
			throw runtime.newTypeError("no suitable constructor found");
		}
		static IRubyObject fitMethodValue(Ruby runtime, java.lang.Object o, java.lang.reflect.Method[] methods, IRubyObject[] args) {
			for (java.lang.reflect.Method method : methods) {
				java.lang.Object[] a = convertArgs(runtime, method.getParameterTypes(), args);
				if (a != null) {
					java.lang.Object j = invoke(o, method, a);
					return makeValueMaybe(runtime, method.getReturnType(), j);
				}
			}
			throw runtime.newTypeError("no suitable method found");
		}
		private static RubyClass __newOuter(Ruby runtime, RubyModule inner) {
			Gate gate = Gate.getCurrent(runtime);
			java.lang.Class<?> type = getJavaClass(inner);
			if (type == null)
				type = gate.r2j.makeInterfaceOrClass(inner);
			java.lang.Class<?> clazz = java.lang.reflect.Array.newInstance(type, 0).getClass();
			RubyClass outer = RubyClass.newClass(runtime, gate.JAVA_ARRAY_CLASS);
			outer.setBaseName(inner.getName().concat("[]"));
			outer.setAllocator(ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
			outer.defineAnnotatedMethods(ObjectArray.class);
			setJavaClass(outer, clazz);
			return outer;
		}
		private static RubyClass __makeOuter(Ruby runtime, RubyModule inner) {
			IRubyObject klass = inner.getInstanceVariable("@__ir_outer");
			if (klass != null)
				return (RubyClass) klass;
			RubyClass outer = __newOuter(runtime, inner);
			inner.setInstanceVariable("@__ir_outer", outer);
			return outer;
		}
		static RubyClass makeOuter(Ruby runtime, RubyModule inner) {
			synchronized(inner) {
				return __makeOuter(runtime, inner);
			}
		}
		static void addStaticFieldReader(RubyModule module, java.lang.String name, final java.lang.reflect.Field field) {
			module.getSingletonClass().addMethod(name, new RubyMethod(module) {
				@Override
				public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule __klass, java.lang.String name) {
					Ruby runtime = self.getRuntime();
					return Util.getField(runtime, null, field);
				}
				@Override
				public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule __klass, java.lang.String name, IRubyObject[] args, Block block) {
					if (args.length == 0)
						return call(context, self, __klass, name);
					Ruby runtime = self.getRuntime();
					throw runtime.newArgumentError("wrong number of arguments");
				}
			});
			return;
		}
		static void addStaticFieldWriter(RubyModule module, java.lang.String name, final java.lang.reflect.Field field) {
			module.getSingletonClass().addMethod(name.concat("="), new RubyMethod(module) {
				@Override
				public RubyNil call(ThreadContext context, IRubyObject self, RubyModule __klass, java.lang.String name, IRubyObject arg0) {
					Ruby runtime = self.getRuntime();
					return Util.setField(runtime, null, field, arg0);
				}
				@Override
				public RubyNil call(ThreadContext context, IRubyObject self, RubyModule __klass, java.lang.String name, IRubyObject[] args, Block block) {
					if (args.length == 1)
						return call(context, self, __klass, name, args[0]);
					Ruby runtime = self.getRuntime();
					throw runtime.newArgumentError("wrong number of arguments");
				}
			});
			return;
		}
		static void addStaticField(RubyModule module, java.lang.String name, final java.lang.reflect.Field field) {
			addStaticFieldReader(module, name, field);
			if (!isFinal(field))
				addStaticFieldWriter(module, name, field);
			return;
		}
		static void addInstanceFieldReader(RubyModule module, java.lang.String name, final java.lang.reflect.Field field) {
			module.addMethod(name, new RubyMethod(module) {
				@Override
				public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule __klass, java.lang.String name) {
					Ruby runtime = self.getRuntime();
					JavaObject proxy = (JavaObject) self;
					return Util.getField(runtime, proxy.getObject(), field);
				}
				@Override
				public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule __klass, java.lang.String name, IRubyObject[] args, Block block) {
					if (args.length == 0)
						return call(context, self, __klass, name);
					Ruby runtime = self.getRuntime();
					throw runtime.newArgumentError("wrong number of arguments");
				}
			});
			return;
		}
		static void addInstanceFieldWriter(RubyModule module, java.lang.String name, final java.lang.reflect.Field field) {
			module.addMethod(name.concat("="), new RubyMethod(module) {
				@Override
				public RubyNil call(ThreadContext context, IRubyObject self, RubyModule __klass, java.lang.String name, IRubyObject arg0) {
					Ruby runtime = self.getRuntime();
					JavaObject proxy = (JavaObject) self;
					return Util.setField(runtime, proxy.getObject(), field, arg0);
				}
				@Override
				public RubyNil call(ThreadContext context, IRubyObject self, RubyModule __klass, java.lang.String name, IRubyObject[] args, Block block) {
					if (args.length == 1)
						return call(context, self, __klass, name, args[0]);
					Ruby runtime = self.getRuntime();
					throw runtime.newArgumentError("wrong number of arguments");
				}
			});
			return;
		}
		static void addInstanceField(RubyModule module, java.lang.String name, final java.lang.reflect.Field field) {
			addInstanceFieldReader(module, name, field);
			if (!isFinal(field))
				addInstanceFieldWriter(module, name, field);
			return;
		}
		static void addStaticFields(RubyModule module, java.lang.Class<?> clazz) {
			java.lang.reflect.Field[] fields = clazz.getDeclaredFields();
			for (java.lang.reflect.Field field : fields)
				if (isStatic(field))
					if (isPublic(field))
						addStaticField(module, field.getName(), field);
			return;
		}
		static void addInstanceFields(RubyModule module, java.lang.Class<?> clazz) {
			java.lang.reflect.Field[] fields = clazz.getDeclaredFields();
			for (java.lang.reflect.Field field : fields)
				if (!isStatic(field))
					if (isPublic(field))
						addInstanceField(module, field.getName(), field);
			return;
		}
		static void addStaticMethod(RubyModule module, java.lang.String name, final java.lang.reflect.Method[] methods) {
			module.getSingletonClass().addMethod(name, new RubyMethod(module) {
				@Override
				public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule __klass, java.lang.String name, IRubyObject[] args, Block block) {
					Ruby runtime = self.getRuntime();
					return Util.fitMethodValue(runtime, null, methods, args);
				}
			});
			return;
		}
		static void addStaticMethods(RubyModule module, MethodMultimap staticMap) {
			for (java.lang.String name : staticMap.keySet()) {
				java.lang.reflect.Method[] staticMethods = staticMap.getMethods(name);
				addStaticMethod(module, name, staticMethods);
			}
			return;
		}
		static void addConstructor(RubyModule module, final java.lang.Class clazz) {
			module.addMethod("initialize", new RubyMethod(module) {
				java.lang.Class<?> pickClass(Ruby runtime, RubyClass klass) {
					if (implementationClass == klass) {
						if (Util.isAbstract(clazz))
							throw runtime.newTypeError("cannot instantiate abstract class");
						return clazz;
					} else {
						if (Util.isFinal(clazz))
							throw runtime.newTypeError("cannot inherit from final class");
						return Gate.getCurrent(runtime).r2j.makeClass(klass);
					}
				}
				@Override
				public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule __klass, java.lang.String name, IRubyObject[] args, Block block) {
					Ruby runtime = self.getRuntime();
					JavaObject proxy = (JavaObject) self;
					RubyClass klass = Util.getRealClass(self);
					java.lang.Class<?> clazz = pickClass(runtime, klass);
					java.lang.Object o = Util.fitConstructor(runtime, clazz, args);
					proxy.setObject(o);
					return (RubyNil) runtime.getNil();
				}
			});
			return;
		}
		static void addCopyMethod(RubyModule module, final java.lang.reflect.Method virtualMethod, final java.lang.reflect.Method superMethod) {
			module.addMethod("initialize_copy", new RubyMethod(module) {
				java.lang.reflect.Method pickMethod(RubyClass klass) {
					if (implementationClass == klass)
						return virtualMethod;
					else
						return superMethod;
				}
				@Override
				public RubyNil call(ThreadContext context, IRubyObject self, RubyModule __klass, java.lang.String name, IRubyObject arg0) {
					Ruby runtime = self.getRuntime();
					JavaObject proxy = (JavaObject) self;
					RubyClass klass = Util.getRealClass(self);
					java.lang.reflect.Method method = pickMethod(klass);
					if (method == null)
						throw runtime.newTypeError("no clone method");
					java.lang.Object o = Util.invoke(((JavaObject) arg0).getObject(), method, new java.lang.Object[0]);
					proxy.setObject(o);
					return (RubyNil) runtime.getNil();
				}
				@Override
				public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule __klass, java.lang.String name, IRubyObject[] args, Block block) {
					if (args.length == 1)
						return call(context, self, __klass, name, args[0]);
					Ruby runtime = self.getRuntime();
					throw runtime.newArgumentError("wrong number of arguments");
				}
			});
			return;
		}
		static void addVirtualMethod(RubyModule module, java.lang.String name, final java.lang.reflect.Method[] virtualMethods, final java.lang.reflect.Method[] superMethods) {
			module.addMethod(name, new RubyMethod(module) {
				java.lang.reflect.Method[] pickMethods(RubyClass klass) {
					if (implementationClass == klass)
						return virtualMethods;
					else
						return superMethods;
				}
				@Override
				public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule __klass, java.lang.String name, IRubyObject[] args, Block block) {
					Ruby runtime = self.getRuntime();
					JavaObject proxy = (JavaObject) self;
					RubyClass klass = Util.getRealClass(self);
					java.lang.reflect.Method[] methods = pickMethods(klass);
					return Util.fitMethodValue(runtime, proxy.getObject(), methods, args);
				}
			});
			return;
		}
		static void addVirtualMethods(RubyModule module, MethodMultimap virtualMap, MethodMultimap superMap) {
			for (java.lang.String name : virtualMap.keySet()) {
				java.lang.reflect.Method[] virtualMethods = virtualMap.getMethods(name);
				java.lang.reflect.Method[] superMethods;
				if (superMap == null)
					superMethods = null;
				else
					superMethods = superMap.getMethods("__ir_super" + '$' + name);
				addVirtualMethod(module, name, virtualMethods, superMethods);
			}
			return;
		}
		static void addInterfaceMembers(RubyModule module, java.lang.Class<?> clazz) {
			addStaticFields(module, clazz);
			return;
		}
		static void addPublicClassMembers(RubyClass klass, java.lang.Class<?> clazz, Factory factory) {
			MethodMultimap virtualMap = buildVirtualMap(clazz);
			java.lang.reflect.Method[] virtualCloneMethods = virtualMap.removeMethods("clone");
			java.lang.reflect.Method virtualCloneMethod = findNullaryMethod(virtualCloneMethods);
			addStaticFields(klass, clazz);
			addStaticMethods(klass, buildStaticMap(clazz));
			addInstanceFields(klass, clazz);
			addConstructor(klass, clazz);
			if (factory == null) {
				addCopyMethod(klass, virtualCloneMethod, null);
				addVirtualMethods(klass, virtualMap, null);
			} else {
				MethodMultimap superMap = buildSuperMap(factory.clazz);
				java.lang.reflect.Method[] superCloneMethods = superMap.removeMethods("__ir_super" + '$' + "clone");
				java.lang.reflect.Method superCloneMethod = findNullaryMethod(superCloneMethods);
				addCopyMethod(klass, virtualCloneMethod, superCloneMethod);
				addVirtualMethods(klass, virtualMap, superMap);
			}
			return;
		}
		static void addPrivateClassMembers(RubyClass klass, java.lang.Class<?> clazz) {
			MethodMultimap virtualMap = buildVirtualMap(clazz);
			java.lang.reflect.Method[] virtualCloneMethods = virtualMap.removeMethods("clone");
			java.lang.reflect.Method virtualCloneMethod = findNullaryMethod(virtualCloneMethods);
			addCopyMethod(klass, virtualCloneMethod, null);
			addVirtualMethods(klass, virtualMap, null);
			return;
		}
		static MethodMultimap buildStaticMap(java.lang.Class<?> clazz) {
			MethodMultimap map = new MethodMultimap();
			java.lang.reflect.Method[] methods = clazz.getDeclaredMethods();
			for (java.lang.reflect.Method method : methods) {
				if (isStatic(method))
					map.addStaticMethod(method);
			}
			return map;
		}
		static MethodMultimap buildVirtualMap(java.lang.Class<?> clazz) {
			MethodMultimap map = new MethodMultimap();
			while (true) {
				if (isPublic(clazz)) {
					java.lang.reflect.Method[] methods = clazz.getDeclaredMethods();
					for (java.lang.reflect.Method method : methods)
						if (!isStatic(method))
							map.addVirtualMethod(method);
				}
				clazz = clazz.getSuperclass();
				if (clazz == null)
					break;
			}
			return map;
		}
		static MethodMultimap buildSuperMap(java.lang.Class<?> clazz) {
			MethodMultimap map = new MethodMultimap();
			java.lang.reflect.Method[] methods = clazz.getDeclaredMethods();
			for (java.lang.reflect.Method method : methods)
				map.addSuperMethod(method);
			return map;
		}
		static java.lang.reflect.Method findNullaryMethod(java.lang.reflect.Method[] methods) {
			for (java.lang.reflect.Method method : methods)
				if (method.getParameterTypes().length == 0)
					return method;
			return null;
		}
		static int hashInt32(int value) {
			return value;
		}
		static int hashInt64(long value) {
			return (int) value ^ (int) (value >> 32);
		}
		static int hashFloat32(float value) {
			return hashInt32(java.lang.Float.floatToRawIntBits(value));
		}
		static int hashFloat64(double value) {
			return hashInt64(java.lang.Double.doubleToRawLongBits(value));
		}
		static RubyString newRubyString(Ruby runtime, byte[] bytes, org.jcodings.Encoding encoding) {
			return RubyString.newString(runtime, new org.jruby.util.ByteList(bytes, encoding));
		}
		static RubyString newRubyString(Ruby runtime, java.lang.String j, org.jcodings.Encoding encoding) {
			return newRubyString(runtime, j.getBytes(java.nio.charset.StandardCharsets.US_ASCII), encoding);
		}
		static long getBignumLongValue(Ruby runtime, IRubyObject o) {
			Gate gate = Gate.getCurrent(runtime);
			ThreadContext context = runtime.getCurrentContext();
			o = gate.INTEGER_X8000000000000000.op_plus(context, o);
			o = gate.INTEGER_XFFFFFFFFFFFFFFFF.op_and(context, o);
			o = gate.INTEGER_MX8000000000000000.op_plus(context, o);
			return ((RubyInteger) o).getLongValue();
		}
		static boolean acceptBoolean(Ruby runtime, IRubyObject o) {
			if (o instanceof RubyBoolean.True)
				return true;
			if (o instanceof RubyBoolean.False)
				return false;
			throw runtime.newTypeError("expected boolean");
		}
		static byte acceptByte(Ruby runtime, IRubyObject o) {
			if (o instanceof Byte)
				return ((Byte) o).value;
			if (o instanceof RubyFixnum)
				return (byte) ((RubyFixnum) o).getLongValue();
			if (o instanceof RubyBignum)
				return (byte) getBignumLongValue(runtime, o);
			throw runtime.newTypeError("expected byte");
		}
		static char acceptChar(Ruby runtime, IRubyObject o) {
			if (o instanceof Char)
				return ((Char) o).value;
			if (o instanceof RubyFixnum)
				return (char) ((RubyFixnum) o).getLongValue();
			if (o instanceof RubyBignum)
				return (char) getBignumLongValue(runtime, o);
			throw runtime.newTypeError("expected char");
		}
		static short acceptInt16(Ruby runtime, IRubyObject o) {
			if (o instanceof Int16)
				return ((Int16) o).value;
			if (o instanceof RubyFixnum)
				return (short) ((RubyFixnum) o).getLongValue();
			if (o instanceof RubyBignum)
				return (short) getBignumLongValue(runtime, o);
			throw runtime.newTypeError("expected int16");
		}
		static int acceptInt32(Ruby runtime, IRubyObject o) {
			if (o instanceof Byte)
				return ((Byte) o).value;
			if (o instanceof Char)
				return ((Char) o).value;
			if (o instanceof Int16)
				return ((Int16) o).value;
			if (o instanceof Int32)
				return ((Int32) o).value;
			if (o instanceof RubyFixnum)
				return (int) ((RubyFixnum) o).getLongValue();
			if (o instanceof RubyBignum)
				return (int) getBignumLongValue(runtime, o);
			throw runtime.newTypeError("expected int32");
		}
		static long acceptInt64(Ruby runtime, IRubyObject o) {
			if (o instanceof Int64)
				return ((Int64) o).value;
			if (o instanceof RubyFixnum)
				return (long) ((RubyFixnum) o).getLongValue();
			if (o instanceof RubyBignum)
				return (long) getBignumLongValue(runtime, o);
			throw runtime.newTypeError("expected int64");
		}
		static float acceptFloat32(Ruby runtime, IRubyObject o) {
			if (o instanceof Float32)
				return ((Float32) o).value;
			if (o instanceof RubyFixnum)
				return (float) ((RubyFixnum) o).getLongValue();
			if (o instanceof RubyBignum)
				return (float) ((RubyBignum) o).getDoubleValue();
			if (o instanceof RubyFloat)
				return (float) ((RubyFloat) o).getDoubleValue();
			throw runtime.newTypeError("expected float32");
		}
		static double acceptFloat64(Ruby runtime, IRubyObject o) {
			if (o instanceof Float64)
				return ((Float64) o).value;
			if (o instanceof RubyFixnum)
				return (double) ((RubyFixnum) o).getLongValue();
			if (o instanceof RubyBignum)
				return (double) ((RubyBignum) o).getDoubleValue();
			if (o instanceof RubyFloat)
				return (double) ((RubyFloat) o).getDoubleValue();
			throw runtime.newTypeError("expected float64");
		}
		static int acceptBoundedInt32(Ruby runtime, IRubyObject o, int n) {
			int i = acceptInt32(runtime, o);
			if (i < 0 || i >= n)
				throw runtime.newArgumentError("index out of bounds");
			return i;
		}
		static int acceptNaturalInt32(Ruby runtime, IRubyObject o) {
			int i = acceptInt32(runtime, o);
			if (i < 0)
				throw runtime.newArgumentError("out of range");
			return i;
		}
		static void arraycopy(Ruby runtime, ObjectArray m0, int i0, ObjectArray m1, int i1, int n) {
			int j0 = i0 + n;
			int j1 = i1 + n;
			if (j0 < 0 || j0 > m0.data.length)
				throw runtime.newArgumentError("index out of bounds");
			if (j1 < 0 || j1 > m1.data.length)
				throw runtime.newArgumentError("index out of bounds");
			while (n-- > 0)
				m1.data[i1++] = m0.data[i0++];
			return;
		}
		static void arraycopy(Ruby runtime, BooleanArray m0, int i0, BooleanArray m1, int i1, int n) {
			int j0 = i0 + n;
			int j1 = i1 + n;
			if (j0 < 0 || j0 > m0.data.length)
				throw runtime.newArgumentError("index out of bounds");
			if (j1 < 0 || j1 > m1.data.length)
				throw runtime.newArgumentError("index out of bounds");
			while (n-- > 0)
				m1.data[i1++] = m0.data[i0++];
			return;
		}
		static void arraycopy(Ruby runtime, ByteArray m0, int i0, ByteArray m1, int i1, int n) {
			int j0 = i0 + n;
			int j1 = i1 + n;
			if (j0 < 0 || j0 > m0.data.length)
				throw runtime.newArgumentError("index out of bounds");
			if (j1 < 0 || j1 > m1.data.length)
				throw runtime.newArgumentError("index out of bounds");
			while (n-- > 0)
				m1.data[i1++] = m0.data[i0++];
			return;
		}
		static void arraycopy(Ruby runtime, CharArray m0, int i0, CharArray m1, int i1, int n) {
			int j0 = i0 + n;
			int j1 = i1 + n;
			if (j0 < 0 || j0 > m0.data.length)
				throw runtime.newArgumentError("index out of bounds");
			if (j1 < 0 || j1 > m1.data.length)
				throw runtime.newArgumentError("index out of bounds");
			while (n-- > 0)
				m1.data[i1++] = m0.data[i0++];
			return;
		}
		static void arraycopy(Ruby runtime, Int16Array m0, int i0, Int16Array m1, int i1, int n) {
			int j0 = i0 + n;
			int j1 = i1 + n;
			if (j0 < 0 || j0 > m0.data.length)
				throw runtime.newArgumentError("index out of bounds");
			if (j1 < 0 || j1 > m1.data.length)
				throw runtime.newArgumentError("index out of bounds");
			while (n-- > 0)
				m1.data[i1++] = m0.data[i0++];
			return;
		}
		static void arraycopy(Ruby runtime, Int32Array m0, int i0, Int32Array m1, int i1, int n) {
			int j0 = i0 + n;
			int j1 = i1 + n;
			if (j0 < 0 || j0 > m0.data.length)
				throw runtime.newArgumentError("index out of bounds");
			if (j1 < 0 || j1 > m1.data.length)
				throw runtime.newArgumentError("index out of bounds");
			while (n-- > 0)
				m1.data[i1++] = m0.data[i0++];
			return;
		}
		static void arraycopy(Ruby runtime, Int64Array m0, int i0, Int64Array m1, int i1, int n) {
			int j0 = i0 + n;
			int j1 = i1 + n;
			if (j0 < 0 || j0 > m0.data.length)
				throw runtime.newArgumentError("index out of bounds");
			if (j1 < 0 || j1 > m1.data.length)
				throw runtime.newArgumentError("index out of bounds");
			while (n-- > 0)
				m1.data[i1++] = m0.data[i0++];
			return;
		}
		static void arraycopy(Ruby runtime, Float32Array m0, int i0, Float32Array m1, int i1, int n) {
			int j0 = i0 + n;
			int j1 = i1 + n;
			if (j0 < 0 || j0 > m0.data.length)
				throw runtime.newArgumentError("index out of bounds");
			if (j1 < 0 || j1 > m1.data.length)
				throw runtime.newArgumentError("index out of bounds");
			while (n-- > 0)
				m1.data[i1++] = m0.data[i0++];
			return;
		}
		static void arraycopy(Ruby runtime, Float64Array m0, int i0, Float64Array m1, int i1, int n) {
			int j0 = i0 + n;
			int j1 = i1 + n;
			if (j0 < 0 || j0 > m0.data.length)
				throw runtime.newArgumentError("index out of bounds");
			if (j1 < 0 || j1 > m1.data.length)
				throw runtime.newArgumentError("index out of bounds");
			while (n-- > 0)
				m1.data[i1++] = m0.data[i0++];
			return;
		}
		static boolean isUpperAlphabetic(char c) {
			return c >= 'A' && c <= 'Z';
		}
		static boolean isLowerAlphabetic(char c) {
			return c >= 'a' && c <= 'z';
		}
		static boolean isMixedAlphanumeric(char c) {
			return c >= '0' && c <= '9' || c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z';
		}
		static boolean isLowerAlphanumeric(char c) {
			return c >= '0' && c <= '9' || c >= 'a' && c <= 'z';
		}
		static boolean isClassName(java.lang.String name) {
			int n = name.length();
			int i = 0;
			if (i >= n)
				return false;
			char c0 = name.charAt(i++);
			if (!isUpperAlphabetic(c0))
				return false;
			while (i < n) {
				char c1 = name.charAt(i++);
				if (!isMixedAlphanumeric(c1))
					return false;
			}
			return true;
		}
		static boolean isPackageName(java.lang.String name) {
			int n = name.length();
			int i = 0;
			if (i >= n)
				return false;
			char c0 = name.charAt(i++);
			if (!isLowerAlphabetic(c0))
				return false;
			while (i < n) {
				char c1 = name.charAt(i++);
				if (!isLowerAlphanumeric(c1))
					return false;
			}
			return true;
		}
		static JavaPackage newPackage(Ruby runtime, java.lang.String name) {
			return new JavaPackage(Gate.getCurrent(runtime).JAVA_PACKAGE_CLASS, name);
		}
		static RubyModule makeModuleOrKlass(Ruby runtime, java.lang.String name) {
			java.lang.Class<?> clazz;
			try {
				clazz = java.lang.Class.forName(name);
			} catch (java.lang.ClassNotFoundException e) {
				throw runtime.newNameError(name, null);
			}
			return Gate.getCurrent(runtime).j2r.makeModuleOrKlass(clazz);
		}
		static IRubyObject splat(Ruby runtime, RubyClass klass, IRubyObject[] args) {
			int dimensions = args.length;
			if (dimensions == 0)
				return klass;
			java.lang.Class<?> clazz = fetchJavaClass(klass);
			java.lang.Class<?> type = clazz.getComponentType();
			int[] counts = new int[dimensions];
			for (int d = 0; d < dimensions; d += 1)
				counts[d] = acceptNaturalInt32(runtime, args[d]);
			return new ArrayFactory(Gate.getCurrent(runtime).ARRAY_FACTORY_CLASS, type, counts);
		}
		static java.lang.Class<?> getInner(java.lang.Class<?> outer) {
			if (outer.isArray()) {
				java.lang.Class<?> inner = outer.getComponentType();
				if (inner.isPrimitive())
					return null;
				return inner;
			}
			return null;
		}
		static RubyBoolean isInheritedBase(Ruby runtime, java.lang.Class<?> clazz0, java.lang.Class<?> clazz1) {
			if (clazz1 == java.lang.Object.class)
				return runtime.getTrue();
			if (clazz1.isAssignableFrom(clazz0))
				return runtime.getTrue();
			return runtime.getFalse();
		}
		static RubyBoolean isInherited(Ruby runtime, java.lang.Class<?> clazz0, java.lang.Class<?> clazz1) {
			java.lang.Class<?> inner0;
			java.lang.Class<?> inner1;
			while (true) {
				inner1 = getInner(clazz1);
				if (inner1 == null)
					return isInheritedBase(runtime, clazz0, clazz1);
				inner0 = getInner(clazz0);
				if (inner0 == null)
					return runtime.getFalse();
				clazz0 = inner0;
				clazz1 = inner1;
			}
		}
		static RubyModule fetchRubyModule(RubyModule wrapper) {
			IRubyObject module = wrapper.getInstanceVariable("@__ir_module");
			if (module != null)
				return (RubyModule) module;
			throw new AssertionError();
		}
		static void setRubyModule(RubyModule wrapper, RubyModule module) {
			wrapper.setInstanceVariable("@__ir_module", module);
			return;
		}
		static RubyModule newKlassArrayFactoryFactory(Ruby runtime, RubyModule module) {
			RubyModule wrapper = RubyModule.newModule(runtime);
			wrapper.defineAnnotatedMethods(KlassArrayFactoryFactory.class);
			setRubyModule(wrapper, module);
			return wrapper;
		}
		static java.lang.Throwable assertThrowable(Ruby runtime, java.lang.Object o) {
			if (o instanceof java.lang.Throwable)
				return (java.lang.Throwable) o;
			throw runtime.newTypeError("expected exception");
		}
		static java.lang.Throwable toThrowable(Ruby runtime, IRubyObject e) {
			if (e instanceof ConcreteJavaProxy) {
				java.lang.Object o = ((ConcreteJavaProxy) e).getObject();
				return assertThrowable(runtime, o);
			}
			if (e instanceof JavaObject) {
				java.lang.Object o = ((JavaObject) e).getObject();
				return assertThrowable(runtime, o);
			}
			if (e instanceof RubyException)
				return new RaiseException((RubyException) e);
			throw runtime.newTypeError("expected exception");
		}
		static java.lang.Throwable makeThrowable(Ruby runtime) {
			IRubyObject e = runtime.getCurrentContext().getErrorInfo();
			if (e.isNil())
				return runtime.newRuntimeError("");
			return toThrowable(runtime, e);
		}
		static java.lang.Throwable makeThrowable(Ruby runtime, IRubyObject arg0) {
			if (arg0 instanceof RubyClass)
				return toThrowable(runtime, arg0.callMethod(runtime.getCurrentContext(), "exception"));
			if (arg0 instanceof RubyString)
				return runtime.newRuntimeError(((RubyString) arg0).toString());
			return toThrowable(runtime, arg0);
		}
		static java.lang.Throwable makeThrowable(Ruby runtime, IRubyObject arg0, IRubyObject arg1) {
			if (arg0 instanceof RubyClass)
				return toThrowable(runtime, arg0.callMethod(runtime.getCurrentContext(), "exception", arg1));
			throw runtime.newRuntimeError("expected exception");
		}
		static java.lang.Throwable makeThrowable(Ruby runtime, IRubyObject[] args) {
			int argc = args.length;
			switch (argc) {
			case 0:
				return makeThrowable(runtime);
			case 1:
				return makeThrowable(runtime, args[0]);
			case 2:
				return makeThrowable(runtime, args[0], args[1]);
			case 3:
				return makeThrowable(runtime, args[0], args[1]);
			default:
				throw runtime.newArgumentError("wrong number of arguments");
			}
		}
	}
	public static interface Adapter {
		static final java.lang.String NAME = "primitive/PrimitiveService$Adapter";
		static final WeakValueIdentityHashMap<RubyBasicObject, Adapter> CACHE = new WeakValueIdentityHashMap<RubyBasicObject, Adapter>();
		public RubyBasicObject __ir_get$object();
		public void __ir_set$object(RubyBasicObject o);
	}
	public static final class Trap {
		static final java.lang.String NAME = "primitive/PrimitiveService$Trap";
		public static void callVoid(RubyBasicObject self, java.lang.String name, IRubyObject[] args) {
			Ruby runtime = self.getRuntime();
			ThreadContext context = runtime.getCurrentContext();
			self.callMethod(context, name, args);
			return;
		}
		public static boolean callBoolean(RubyBasicObject self, java.lang.String name, IRubyObject[] args) {
			Ruby runtime = self.getRuntime();
			ThreadContext context = runtime.getCurrentContext();
			IRubyObject r = self.callMethod(context, name, args);
			if (r instanceof RubyBoolean.True)
				return true;
			if (r instanceof RubyBoolean.False)
				return false;
			throw runtime.newTypeError("expected boolean");
		}
		public static byte callByte(RubyBasicObject self, java.lang.String name, IRubyObject[] args) {
			Ruby runtime = self.getRuntime();
			ThreadContext context = runtime.getCurrentContext();
			IRubyObject r = self.callMethod(context, name, args);
			if (r instanceof Byte)
				return ((Byte) r).value;
			throw runtime.newTypeError("expected byte");
		}
		public static char callChar(RubyBasicObject self, java.lang.String name, IRubyObject[] args) {
			Ruby runtime = self.getRuntime();
			ThreadContext context = runtime.getCurrentContext();
			IRubyObject r = self.callMethod(context, name, args);
			if (r instanceof Char)
				return ((Char) r).value;
			throw runtime.newTypeError("expected char");
		}
		public static short callInt16(RubyBasicObject self, java.lang.String name, IRubyObject[] args) {
			Ruby runtime = self.getRuntime();
			ThreadContext context = runtime.getCurrentContext();
			IRubyObject r = self.callMethod(context, name, args);
			if (r instanceof Int16)
				return ((Int16) r).value;
			throw runtime.newTypeError("expected int16");
		}
		public static int callInt32(RubyBasicObject self, java.lang.String name, IRubyObject[] args) {
			Ruby runtime = self.getRuntime();
			ThreadContext context = runtime.getCurrentContext();
			IRubyObject r = self.callMethod(context, name, args);
			if (r instanceof Int32)
				return ((Int32) r).value;
			throw runtime.newTypeError("expected int32");
		}
		public static long callInt64(RubyBasicObject self, java.lang.String name, IRubyObject[] args) {
			Ruby runtime = self.getRuntime();
			ThreadContext context = runtime.getCurrentContext();
			IRubyObject r = self.callMethod(context, name, args);
			if (r instanceof Int64)
				return ((Int64) r).value;
			throw runtime.newTypeError("expected int64");
		}
		public static float callFloat32(RubyBasicObject self, java.lang.String name, IRubyObject[] args) {
			Ruby runtime = self.getRuntime();
			ThreadContext context = runtime.getCurrentContext();
			IRubyObject r = self.callMethod(context, name, args);
			if (r instanceof Float32)
				return ((Float32) r).value;
			throw runtime.newTypeError("expected float32");
		}
		public static double callFloat64(RubyBasicObject self, java.lang.String name, IRubyObject[] args) {
			Ruby runtime = self.getRuntime();
			ThreadContext context = runtime.getCurrentContext();
			IRubyObject r = self.callMethod(context, name, args);
			if (r instanceof Float64)
				return ((Float64) r).value;
			throw runtime.newTypeError("expected float64");
		}
		public static java.lang.Object callReference(RubyBasicObject self, java.lang.String name, IRubyObject[] args) {
			Ruby runtime = self.getRuntime();
			ThreadContext context = runtime.getCurrentContext();
			IRubyObject r = self.callMethod(context, name, args);
			return Util.makeAdapterUnlessProxyMaybe(runtime, r);
		}
		public static IRubyObject newBoolean(Ruby runtime, boolean j) {
			return RubyBoolean.newBoolean(runtime, j);
		}
		public static IRubyObject newByte(Ruby runtime, byte j) {
			return Util.newByte(runtime, j);
		}
		public static IRubyObject newChar(Ruby runtime, char j) {
			return Util.newChar(runtime, j);
		}
		public static IRubyObject newInt16(Ruby runtime, short j) {
			return Util.newInt16(runtime, j);
		}
		public static IRubyObject newInt32(Ruby runtime, int j) {
			return Util.newInt32(runtime, j);
		}
		public static IRubyObject newInt64(Ruby runtime, long j) {
			return Util.newInt64(runtime, j);
		}
		public static IRubyObject newFloat32(Ruby runtime, float j) {
			return Util.newFloat32(runtime, j);
		}
		public static IRubyObject newFloat64(Ruby runtime, double j) {
			return Util.newFloat64(runtime, j);
		}
		public static IRubyObject makeProxyUnlessAdapterMaybe(Ruby runtime, java.lang.Object j) {
			return Util.makeProxyUnlessAdapterMaybe(runtime, j);
		}
	}
	public static final class ClassMixin {
		@JRubyMethod(meta = true, name = "~")
		public static Proxy __inv(IRubyObject self) {
			Ruby runtime = self.getRuntime();
			java.lang.Class<?> clazz = Util.fetchJavaClass((RubyModule) self);
			return Gate.getCurrent(runtime).newClassProxy(clazz);
		}
		@JRubyMethod(meta = true, name = "[]", rest = true)
		public static IRubyObject __splat(IRubyObject self, IRubyObject[] args) {
			Ruby runtime = self.getRuntime();
			RubyClass klass = Util.makeOuter(runtime, (RubyModule) self);
			return Util.splat(runtime, klass, args);
		}
		@JRubyMethod(meta = true)
		public static IRubyObject const_missing(IRubyObject self, IRubyObject symbol) {
			Ruby runtime = self.getRuntime();
			java.lang.String name = symbol.asJavaString();
			java.lang.Class<?> clazz = Util.getJavaClass((RubyModule) self);
			if (clazz == null) {
				IRubyObject r = runtime.getObject().getConstantAt(name);
				if (r != null)
					return r;
				throw runtime.newNameError(name, null);
			}
			java.lang.reflect.Field field;
			try {
				field = clazz.getField(name);
			} catch (java.lang.NoSuchFieldException e) {
				return Util.makeModuleOrKlass(runtime, clazz.getName() + '$' + name);
			}
			if (Util.isStatic(field))
				return Util.getField(runtime, null, field);
			return Util.makeModuleOrKlass(runtime, clazz.getName() + '$' + name);
		}
	}
	public static final class KernelMixin {
		@JRubyMethod(name = "raise", optional = 3)
		public static RubyNil __raise(IRubyObject self, IRubyObject[] args) {
			Ruby runtime = self.getRuntime();
			Helpers.throwException(Util.makeThrowable(runtime, args));
			return (RubyNil) runtime.getNil();
		}
	}
	public static final class StringMixin {
		@JRubyMethod(name = "from_j", meta = true)
		public static RubyString __from_j(IRubyObject self, IRubyObject s) {
			Ruby runtime = self.getRuntime();
			if (s instanceof JavaObject) {
				java.lang.Object o = ((JavaObject) s).getObject();
				if (o instanceof java.lang.String)
					return RubyString.newString(runtime, (java.lang.String) o);
			}
			throw runtime.newTypeError("expected java.lang.String");
		}
		@JRubyMethod(name = "from_j_bytes", meta = true)
		public static RubyString __from_j_bytes(IRubyObject self, IRubyObject m) {
			Ruby runtime = self.getRuntime();
			if (m instanceof ByteArray) {
				byte[] data = ((ByteArray) m).data;
				return RubyString.newString(runtime, data);
			}
			throw runtime.newTypeError("expected byte[]");
		}
		@JRubyMethod(name = "to_j")
		public static Proxy __to_j(IRubyObject self) {
			Ruby runtime = self.getRuntime();
			java.lang.String j = ((RubyString) self).decodeString();
			return Gate.getCurrent(runtime).newStringProxy(j);
		}
		@JRubyMethod(name = "to_j_bytes")
		public static ByteArray __to_j_bytes(IRubyObject self) {
			Ruby runtime = self.getRuntime();
			byte[] data = ((RubyString) self).getBytes();
			return new ByteArray(Gate.getCurrent(runtime).BYTE_ARRAY_CLASS, data);
		}
	}
	public static final class JavaPackage extends RubyBasicObject {
		final java.lang.String name;
		JavaPackage(RubyClass metaClass, java.lang.String name) {
			super(metaClass);
			this.name = name;
			return;
		}
		@JRubyMethod(name = "inspect")
		public RubyString __inspect() {
			Ruby runtime = getRuntime();
			return RubyString.newString(runtime, name);
		}
		@JRubyMethod(name = "nil?")
		public RubyBoolean __is$nil() {
			Ruby runtime = getRuntime();
			return runtime.getFalse();
		}
		@JRubyMethod
		public IRubyObject method_missing(IRubyObject symbol) {
			Ruby runtime = getRuntime();
			java.lang.String name = symbol.asJavaString();
			if (Util.isPackageName(name))
				return Util.newPackage(runtime, this.name + '.' + name);
			if (Util.isClassName(name))
				return Util.makeModuleOrKlass(runtime, this.name + '.' + name);
			throw runtime.newNoMethodError(name, null, null);
		}
	}
	public static final class Box extends RubyObject {
		final java.lang.Object e;
		Box(RubyClass metaClass, java.lang.Object e) {
			super(metaClass);
			this.e = e;
			return;
		}
		java.lang.Object getObject() {
			return e;
		}
	}
	public static interface Proxy extends IRubyObject {
		static final WeakValueIdentityHashMap<java.lang.Object, Proxy> CACHE = new WeakValueIdentityHashMap<java.lang.Object, Proxy>();
		public java.lang.Object getObject();
	}
	public static abstract class JavaValue extends RubyBasicObject {
		@JRubyMethod(meta = true)
		public static IRubyObject const_missing(IRubyObject self, IRubyObject symbol) {
			Ruby runtime = self.getRuntime();
			java.lang.String name = symbol.asJavaString();
			IRubyObject	r = runtime.getObject().getConstantAt(name);
			if (r != null)
				return r;
			throw runtime.newNameError(name, null);
		}
		JavaValue(RubyClass metaClass) {
			super(metaClass);
			return;
		}
	}
	public static abstract class JavaBasicObject extends JavaValue implements Proxy {
		@JRubyMethod(meta = true, name = "===")
		public static RubyBoolean __eqv(IRubyObject self, IRubyObject o) {
			Ruby runtime = self.getRuntime();
			if (o instanceof ConcreteJavaProxy) {
				java.lang.Class<?> clazz = Util.fetchJavaClass((RubyClass) self);
				if (clazz.isInstance(((ConcreteJavaProxy) o).getObject()))
					return runtime.getTrue();
			}
			return runtime.newBoolean(((RubyClass) self).isInstance(o));
		}
		JavaBasicObject(RubyClass metaClass) {
			super(metaClass);
			return;
		}
	}
	public static abstract class JavaArray extends JavaValue implements Proxy {
		JavaArray(RubyClass metaClass) {
			super(metaClass);
			return;
		}
		@JRubyMethod(name = "equals")
		public RubyBoolean __equals(IRubyObject o) {
			Ruby runtime = getRuntime();
			return runtime.newBoolean(this == o);
		}
		@JRubyMethod(name = "hashCode")
		public Int32 __hashCode() {
			Ruby runtime = getRuntime();
			return new Int32(Gate.getCurrent(runtime).INT32_CLASS, java.lang.System.identityHashCode(this));
		}
		@JRubyMethod(name = "toString")
		public Proxy __toString() {
			Ruby runtime = getRuntime();
			java.lang.String j = ((RubyString) anyToString()).decodeString();
			return Gate.getCurrent(runtime).newStringProxy(j);
		}
		@JRubyMethod(name = "eql?")
		public RubyBoolean __is$eql(IRubyObject o) {
			Ruby runtime = getRuntime();
			return runtime.newBoolean(this == o);
		}
		@JRubyMethod(name = "hash")
		public RubyFixnum __hash() {
			Ruby runtime = getRuntime();
			return RubyFixnum.newFixnum(runtime, (long) java.lang.System.identityHashCode(this));
		}
		@JRubyMethod(name = "to_s")
		public RubyString __to_s() {
			return (RubyString) anyToString();
		}
	}
	public static final class JavaObject extends JavaBasicObject {
		static final ObjectAllocator ALLOCATOR = new ObjectAllocator() {
			@Override
			public Proxy allocate(Ruby runtime, RubyClass metaClass) {
				return new JavaObject(metaClass);
			}
		};
		java.lang.Object o;
		JavaObject(RubyClass metaClass) {
			super(metaClass);
			return;
		}
		JavaObject(RubyClass metaClass, java.lang.Object o) {
			super(metaClass);
			this.o = o;
			return;
		}
		public java.lang.Object getObject() {
			return this.o;
		}
		public void setObject(java.lang.Object o) {
			if (o instanceof Adapter)
				((Adapter) o).__ir_set$object(this);
			this.o = o;
			return;
		}
	}
	public static final class ArrayFactory extends RubyObject {
		final java.lang.Class<?> type;
		final int[] counts;
		ArrayFactory(RubyClass metaClass, java.lang.Class<?> type, int[] counts) {
			super(metaClass);
			this.type = type;
			this.counts = counts;
			return;
		}
		@JRubyMethod(name = "new")
		public Proxy __new() {
			Ruby runtime = getRuntime();
			java.lang.Object j = java.lang.reflect.Array.newInstance(type, counts);
			return Gate.getCurrent(runtime).newArrayProxy(j.getClass().getComponentType(), j);
		}
	}
	public static final class PrimitiveArrayFactoryFactory extends RubyObject {
		final RubyClass klass;
		PrimitiveArrayFactoryFactory(RubyClass metaClass, RubyClass klass) {
			super(metaClass);
			this.klass = klass;
			return;
		}
		@JRubyMethod(name = "~")
		public Proxy __inv() {
			Ruby runtime = getRuntime();
			java.lang.Class<?> clazz = Util.fetchJavaClass(klass);
			return Gate.getCurrent(runtime).newClassProxy(clazz.getComponentType());
		}
		@JRubyMethod(name = "[]", rest = true)
		public IRubyObject __splat(IRubyObject[] args) {
			Ruby runtime = getRuntime();
			return Util.splat(runtime, klass, args);
		}
	}
	public static final class ObjectArray extends JavaArray {
		@JRubyMethod(meta = true, name = "~")
		public static Proxy __inv(IRubyObject self) {
			Ruby runtime = self.getRuntime();
			java.lang.Class<?> clazz = Util.fetchJavaClass((RubyClass) self);
			return Gate.getCurrent(runtime).newClassProxy(clazz);
		}
		@JRubyMethod(meta = true, name = "[]", rest = true)
		public static IRubyObject __splat(IRubyObject self, IRubyObject[] args) {
			Ruby runtime = self.getRuntime();
			RubyClass klass = Util.makeOuter(runtime, (RubyClass) self);
			return Util.splat(runtime, klass, args);
		}
		@JRubyMethod(meta = true, name = "new", rest = true)
		public static ObjectArray __new(IRubyObject self, IRubyObject[] args) {
			Ruby runtime = self.getRuntime();
			java.lang.Class<?> clazz = Util.fetchJavaClass((RubyClass) self);
			java.lang.Class<?> type = clazz.getComponentType();
			int argc = args.length;
			if (argc == 0) {
				java.lang.Object[] data = (java.lang.Object[]) java.lang.reflect.Array.newInstance(type, 0);
				return new ObjectArray((RubyClass) self, data);
			}
			if (argc == 1) {
				int n = Util.acceptNaturalInt32(runtime, args[0]);
				java.lang.Object[] data = (java.lang.Object[]) java.lang.reflect.Array.newInstance(type, n);
				return new ObjectArray((RubyClass) self, data);
			}
			if (argc == 2) {
				int n = Util.acceptNaturalInt32(runtime, args[0]);
				java.lang.Object[] data = (java.lang.Object[]) java.lang.reflect.Array.newInstance(type, n);
				java.util.Arrays.fill(data, Util.makeAdapterUnlessProxyMaybe(runtime, args[1]));
				return new ObjectArray((RubyClass) self, data);
			}
			throw runtime.newArgumentError("wrong number of arguments");
		}
		@JRubyMethod(meta = true, name = "of", rest = true)
		public static ObjectArray __of(IRubyObject self, IRubyObject[] args) {
			Ruby runtime = self.getRuntime();
			int n = args.length;
			java.lang.Class<?> clazz = Util.fetchJavaClass((RubyClass) self);
			java.lang.Class<?> type = clazz.getComponentType();
			java.lang.Object[] data = (java.lang.Object[]) java.lang.reflect.Array.newInstance(type, n);
			for (int i = 0; i < n; i += 1)
				data[i] = Util.makeAdapterUnlessProxyMaybe(runtime, args[i]);
			return new ObjectArray((RubyClass) self, data);
		}
		@JRubyMethod(meta = true, name = "===")
		public static RubyBoolean __eqv(IRubyObject self, IRubyObject o) {
			Ruby runtime = self.getRuntime();
			java.lang.Class<?> clazz = Util.getJavaClass(o.getMetaClass());
			if (clazz == null)
				return runtime.getFalse();
			return Util.isInherited(runtime, clazz, Util.fetchJavaClass((RubyClass) self));
		}
		final java.lang.Object[] data;
		ObjectArray(RubyClass metaClass, java.lang.Object[] data) {
			super(metaClass);
			this.data = data;
			return;
		}
		public java.lang.Object getObject() {
			return data;
		}
		@JRubyMethod(name = "is_a?")
		public RubyBoolean __is$is_a(IRubyObject module) {
			Ruby runtime = getRuntime();
			if (module instanceof RubyModule) {
				java.lang.Class<?> clazz = Util.getJavaClass((RubyModule) module);
				if (clazz == null)
					return runtime.getFalse();
				return Util.isInherited(runtime, Util.fetchJavaClass(getMetaClass()), clazz);
			}
			throw runtime.newTypeError("class or module required");
		}
		@JRubyMethod(name = "clone")
		public ObjectArray __clone() {
			return new ObjectArray(getMetaClass(), data.clone());
		}
		@JRubyMethod(name = "dup")
		public ObjectArray __dup() {
			return new ObjectArray(getMetaClass(), data.clone());
		}
		@JRubyMethod(name = "length")
		public Int32 __length() {
			Ruby runtime = getRuntime();
			return new Int32(Gate.getCurrent(runtime).INT32_CLASS, data.length);
		}
		@JRubyMethod(name = "[]")
		public IRubyObject __get(IRubyObject index) {
			Ruby runtime = getRuntime();
			int i = Util.acceptBoundedInt32(runtime, index, data.length);
			return Util.makeProxyUnlessAdapterMaybe(runtime, data[i]);
		}
		@JRubyMethod(name = "[]=")
		public RubyNil __set(IRubyObject index, IRubyObject e) {
			Ruby runtime = getRuntime();
			int i = Util.acceptBoundedInt32(runtime, index, data.length);
			data[i] = Util.makeAdapterUnlessProxyMaybe(runtime, e);
			return (RubyNil) runtime.getNil();
		}
		@JRubyMethod(name = "copy", required = 4)
		public RubyNil __copy(IRubyObject[] args) {
			Ruby runtime = getRuntime();
			IRubyObject i0 = args[0];
			IRubyObject m1 = args[1];
			IRubyObject i1 = args[2];
			IRubyObject n = args[3];
			if (m1 instanceof ObjectArray) {
				Util.arraycopy(runtime, this, Util.acceptNaturalInt32(runtime, i0), (ObjectArray) m1, Util.acceptNaturalInt32(runtime, i1), Util.acceptNaturalInt32(runtime, n));
				return (RubyNil) runtime.getNil();
			}
			throw runtime.newTypeError("expected array");
		}
		@JRubyMethod(name = "each")
		public RubyNil __each(Block block) {
			Ruby runtime = getRuntime();
			ThreadContext context = runtime.getCurrentContext();
			int i = 0;
			while (i < data.length) {
				block.call(context, Util.makeProxyUnlessAdapterMaybe(runtime, data[i]));
				i += 1;
			}
			return (RubyNil) runtime.getNil();
		}
	}
	public static final class BooleanArray extends JavaArray {
		@JRubyMethod(meta = true, name = "~")
		public static Proxy __inv(IRubyObject self) {
			Ruby runtime = self.getRuntime();
			java.lang.Class<?> clazz = Util.fetchJavaClass((RubyClass) self);
			return Gate.getCurrent(runtime).newClassProxy(clazz);
		}
		@JRubyMethod(meta = true, name = "[]", rest = true)
		public static IRubyObject __splat(IRubyObject self, IRubyObject[] args) {
			Ruby runtime = self.getRuntime();
			RubyClass klass = Util.makeOuter(runtime, (RubyClass) self);
			return Util.splat(runtime, klass, args);
		}
		@JRubyMethod(meta = true, name = "new", rest = true)
		public static BooleanArray __new(IRubyObject self, IRubyObject[] args) {
			Ruby runtime = self.getRuntime();
			int argc = args.length;
			if (argc == 0) {
				boolean[] data = new boolean[0];
				return new BooleanArray((RubyClass) self, data);
			}
			if (argc == 1) {
				int n = Util.acceptNaturalInt32(runtime, args[0]);
				boolean[] data = new boolean[n];
				return new BooleanArray((RubyClass) self, data);
			}
			if (argc == 2) {
				int n = Util.acceptNaturalInt32(runtime, args[0]);
				boolean[] data = new boolean[n];
				java.util.Arrays.fill(data, Util.acceptBoolean(runtime, args[1]));
				return new BooleanArray((RubyClass) self, data);
			}
			throw runtime.newArgumentError("wrong number of arguments");
		}
		@JRubyMethod(meta = true, name = "of", rest = true)
		public static BooleanArray __of(IRubyObject self, IRubyObject[] args) {
			Ruby runtime = self.getRuntime();
			int n = args.length;
			boolean[] data = new boolean[n];
			for (int i = 0; i < n; i += 1)
				data[i] = Util.acceptBoolean(runtime, args[i]);
			return new BooleanArray((RubyClass) self, data);
		}
		final boolean[] data;
		BooleanArray(RubyClass metaClass, boolean[] data) {
			super(metaClass);
			this.data = data;
			return;
		}
		public java.lang.Object getObject() {
			return data;
		}
		@JRubyMethod(name = "is_a?")
		public RubyBoolean __is$is_a(IRubyObject module) {
			Ruby runtime = getRuntime();
			if (module instanceof RubyModule) {
				if (module == getMetaClass())
					return runtime.getTrue();
				if (module == Gate.getCurrent(runtime).JAVA_LANG_OBJECT_CLASS)
					return runtime.getTrue();
				return runtime.getFalse();
			}
			throw runtime.newTypeError("class or module required");
		}
		@JRubyMethod(name = "clone")
		public BooleanArray __clone() {
			return new BooleanArray(getMetaClass(), data.clone());
		}
		@JRubyMethod(name = "dup")
		public BooleanArray __dup() {
			return new BooleanArray(getMetaClass(), data.clone());
		}
		@JRubyMethod(name = "length")
		public Int32 __length() {
			Ruby runtime = getRuntime();
			return new Int32(Gate.getCurrent(runtime).INT32_CLASS, data.length);
		}
		@JRubyMethod(name = "[]")
		public RubyBoolean __get(IRubyObject index) {
			Ruby runtime = getRuntime();
			int i = Util.acceptBoundedInt32(runtime, index, data.length);
			return Util.newBoolean(runtime, data[i]);
		}
		@JRubyMethod(name = "[]=")
		public RubyNil __set(IRubyObject index, IRubyObject e) {
			Ruby runtime = getRuntime();
			int i = Util.acceptBoundedInt32(runtime, index, data.length);
			data[i] = Util.acceptBoolean(runtime, e);
			return (RubyNil) runtime.getNil();
		}
		@JRubyMethod(name = "copy", required = 4)
		public RubyNil __copy(IRubyObject[] args) {
			Ruby runtime = getRuntime();
			IRubyObject i0 = args[0];
			IRubyObject m1 = args[1];
			IRubyObject i1 = args[2];
			IRubyObject n = args[3];
			if (m1 instanceof BooleanArray) {
				Util.arraycopy(runtime, this, Util.acceptNaturalInt32(runtime, i0), (BooleanArray) m1, Util.acceptNaturalInt32(runtime, i1), Util.acceptNaturalInt32(runtime, n));
				return (RubyNil) runtime.getNil();
			}
			throw runtime.newTypeError("expected array");
		}
		@JRubyMethod(name = "each")
		public RubyNil __each(Block block) {
			Ruby runtime = getRuntime();
			ThreadContext context = runtime.getCurrentContext();
			int i = 0;
			while (i < data.length) {
				block.call(context, Util.newBoolean(runtime, data[i]));
				i += 1;
			}
			return (RubyNil) runtime.getNil();
		}
	}
	public static final class ByteArray extends JavaArray {
		@JRubyMethod(meta = true, name = "~")
		public static Proxy __inv(IRubyObject self) {
			Ruby runtime = self.getRuntime();
			java.lang.Class<?> clazz = Util.fetchJavaClass((RubyClass) self);
			return Gate.getCurrent(runtime).newClassProxy(clazz);
		}
		@JRubyMethod(meta = true, name = "[]", rest = true)
		public static IRubyObject __splat(IRubyObject self, IRubyObject[] args) {
			Ruby runtime = self.getRuntime();
			RubyClass klass = Util.makeOuter(runtime, (RubyClass) self);
			return Util.splat(runtime, klass, args);
		}
		@JRubyMethod(meta = true, name = "new", rest = true)
		public static ByteArray __new(IRubyObject self, IRubyObject[] args) {
			Ruby runtime = self.getRuntime();
			int argc = args.length;
			if (argc == 0) {
				byte[] data = new byte[0];
				return new ByteArray((RubyClass) self, data);
			}
			if (argc == 1) {
				int n = Util.acceptNaturalInt32(runtime, args[0]);
				byte[] data = new byte[n];
				return new ByteArray((RubyClass) self, data);
			}
			if (argc == 2) {
				int n = Util.acceptNaturalInt32(runtime, args[0]);
				byte[] data = new byte[n];
				java.util.Arrays.fill(data, Util.acceptByte(runtime, args[1]));
				return new ByteArray((RubyClass) self, data);
			}
			throw runtime.newArgumentError("wrong number of arguments");
		}
		@JRubyMethod(meta = true, name = "of", rest = true)
		public static ByteArray __of(IRubyObject self, IRubyObject[] args) {
			Ruby runtime = self.getRuntime();
			int n = args.length;
			byte[] data = new byte[n];
			for (int i = 0; i < n; i += 1)
				data[i] = Util.acceptByte(runtime, args[i]);
			return new ByteArray((RubyClass) self, data);
		}
		final byte[] data;
		ByteArray(RubyClass metaClass, byte[] data) {
			super(metaClass);
			this.data = data;
			return;
		}
		public java.lang.Object getObject() {
			return data;
		}
		@JRubyMethod(name = "is_a?")
		public RubyBoolean __is$is_a(IRubyObject module) {
			Ruby runtime = getRuntime();
			if (module instanceof RubyModule) {
				if (module == getMetaClass())
					return runtime.getTrue();
				if (module == Gate.getCurrent(runtime).JAVA_LANG_OBJECT_CLASS)
					return runtime.getTrue();
				return runtime.getFalse();
			}
			throw runtime.newTypeError("class or module required");
		}
		@JRubyMethod(name = "clone")
		public ByteArray __clone() {
			return new ByteArray(getMetaClass(), data.clone());
		}
		@JRubyMethod(name = "dup")
		public ByteArray __dup() {
			return new ByteArray(getMetaClass(), data.clone());
		}
		@JRubyMethod(name = "length")
		public Int32 __length() {
			Ruby runtime = getRuntime();
			return new Int32(Gate.getCurrent(runtime).INT32_CLASS, data.length);
		}
		@JRubyMethod(name = "[]")
		public Byte __get(IRubyObject index) {
			Ruby runtime = getRuntime();
			int i = Util.acceptBoundedInt32(runtime, index, data.length);
			return Util.newByte(runtime, data[i]);
		}
		@JRubyMethod(name = "[]=")
		public RubyNil __set(IRubyObject index, IRubyObject e) {
			Ruby runtime = getRuntime();
			int i = Util.acceptBoundedInt32(runtime, index, data.length);
			data[i] = Util.acceptByte(runtime, e);
			return (RubyNil) runtime.getNil();
		}
		@JRubyMethod(name = "copy", required = 4)
		public RubyNil __copy(IRubyObject[] args) {
			Ruby runtime = getRuntime();
			IRubyObject i0 = args[0];
			IRubyObject m1 = args[1];
			IRubyObject i1 = args[2];
			IRubyObject n = args[3];
			if (m1 instanceof ByteArray) {
				Util.arraycopy(runtime, this, Util.acceptNaturalInt32(runtime, i0), (ByteArray) m1, Util.acceptNaturalInt32(runtime, i1), Util.acceptNaturalInt32(runtime, n));
				return (RubyNil) runtime.getNil();
			}
			throw runtime.newTypeError("expected array");
		}
		@JRubyMethod(name = "each")
		public RubyNil __each(Block block) {
			Ruby runtime = getRuntime();
			ThreadContext context = runtime.getCurrentContext();
			int i = 0;
			while (i < data.length) {
				block.call(context, Util.newByte(runtime, data[i]));
				i += 1;
			}
			return (RubyNil) runtime.getNil();
		}
		@JRubyMethod(name = "to_s")
		public RubyString __to_s() {
			Ruby runtime = getRuntime();
			return RubyString.newString(runtime, data);
		}
		@JRubyMethod(name = "inspect")
		public RubyString __inspect() {
			return (RubyString) anyToString();
		}
	}
	public static final class CharArray extends JavaArray {
		@JRubyMethod(meta = true, name = "~")
		public static Proxy __inv(IRubyObject self) {
			Ruby runtime = self.getRuntime();
			java.lang.Class<?> clazz = Util.fetchJavaClass((RubyClass) self);
			return Gate.getCurrent(runtime).newClassProxy(clazz);
		}
		@JRubyMethod(meta = true, name = "[]", rest = true)
		public static IRubyObject __splat(IRubyObject self, IRubyObject[] args) {
			Ruby runtime = self.getRuntime();
			RubyClass klass = Util.makeOuter(runtime, (RubyClass) self);
			return Util.splat(runtime, klass, args);
		}
		@JRubyMethod(meta = true, name = "new", rest = true)
		public static CharArray __new(IRubyObject self, IRubyObject[] args) {
			Ruby runtime = self.getRuntime();
			int argc = args.length;
			if (argc == 0) {
				char[] data = new char[0];
				return new CharArray((RubyClass) self, data);
			}
			if (argc == 1) {
				int n = Util.acceptNaturalInt32(runtime, args[0]);
				char[] data = new char[n];
				return new CharArray((RubyClass) self, data);
			}
			if (argc == 2) {
				int n = Util.acceptNaturalInt32(runtime, args[0]);
				char[] data = new char[n];
				java.util.Arrays.fill(data, Util.acceptChar(runtime, args[1]));
				return new CharArray((RubyClass) self, data);
			}
			throw runtime.newArgumentError("wrong number of arguments");
		}
		@JRubyMethod(meta = true, name = "of", rest = true)
		public static CharArray __of(IRubyObject self, IRubyObject[] args) {
			Ruby runtime = self.getRuntime();
			int n = args.length;
			char[] data = new char[n];
			for (int i = 0; i < n; i += 1)
				data[i] = Util.acceptChar(runtime, args[i]);
			return new CharArray((RubyClass) self, data);
		}
		final char[] data;
		CharArray(RubyClass metaClass, char[] data) {
			super(metaClass);
			this.data = data;
			return;
		}
		public java.lang.Object getObject() {
			return data;
		}
		@JRubyMethod(name = "is_a?")
		public RubyBoolean __is$is_a(IRubyObject module) {
			Ruby runtime = getRuntime();
			if (module instanceof RubyModule) {
				if (module == getMetaClass())
					return runtime.getTrue();
				if (module == Gate.getCurrent(runtime).JAVA_LANG_OBJECT_CLASS)
					return runtime.getTrue();
				return runtime.getFalse();
			}
			throw runtime.newTypeError("class or module required");
		}
		@JRubyMethod(name = "clone")
		public CharArray __clone() {
			return new CharArray(getMetaClass(), data.clone());
		}
		@JRubyMethod(name = "dup")
		public CharArray __dup() {
			return new CharArray(getMetaClass(), data.clone());
		}
		@JRubyMethod(name = "length")
		public Int32 __length() {
			Ruby runtime = getRuntime();
			return new Int32(Gate.getCurrent(runtime).INT32_CLASS, data.length);
		}
		@JRubyMethod(name = "[]")
		public Char __get(IRubyObject index) {
			Ruby runtime = getRuntime();
			int i = Util.acceptBoundedInt32(runtime, index, data.length);
			return Util.newChar(runtime, data[i]);
		}
		@JRubyMethod(name = "[]=")
		public RubyNil __set(IRubyObject index, IRubyObject e) {
			Ruby runtime = getRuntime();
			int i = Util.acceptBoundedInt32(runtime, index, data.length);
			data[i] = Util.acceptChar(runtime, e);
			return (RubyNil) runtime.getNil();
		}
		@JRubyMethod(name = "copy", required = 4)
		public RubyNil __copy(IRubyObject[] args) {
			Ruby runtime = getRuntime();
			IRubyObject i0 = args[0];
			IRubyObject m1 = args[1];
			IRubyObject i1 = args[2];
			IRubyObject n = args[3];
			if (m1 instanceof CharArray) {
				Util.arraycopy(runtime, this, Util.acceptNaturalInt32(runtime, i0), (CharArray) m1, Util.acceptNaturalInt32(runtime, i1), Util.acceptNaturalInt32(runtime, n));
				return (RubyNil) runtime.getNil();
			}
			throw runtime.newTypeError("expected array");
		}
		@JRubyMethod(name = "each")
		public RubyNil __each(Block block) {
			Ruby runtime = getRuntime();
			ThreadContext context = runtime.getCurrentContext();
			int i = 0;
			while (i < data.length) {
				block.call(context, Util.newChar(runtime, data[i]));
				i += 1;
			}
			return (RubyNil) runtime.getNil();
		}
	}
	public static final class Int16Array extends JavaArray {
		@JRubyMethod(meta = true, name = "~")
		public static Proxy __inv(IRubyObject self) {
			Ruby runtime = self.getRuntime();
			java.lang.Class<?> clazz = Util.fetchJavaClass((RubyClass) self);
			return Gate.getCurrent(runtime).newClassProxy(clazz);
		}
		@JRubyMethod(meta = true, name = "[]", rest = true)
		public static IRubyObject __splat(IRubyObject self, IRubyObject[] args) {
			Ruby runtime = self.getRuntime();
			RubyClass klass = Util.makeOuter(runtime, (RubyClass) self);
			return Util.splat(runtime, klass, args);
		}
		@JRubyMethod(meta = true, name = "new", rest = true)
		public static Int16Array __new(IRubyObject self, IRubyObject[] args) {
			Ruby runtime = self.getRuntime();
			int argc = args.length;
			if (argc == 0) {
				short[] data = new short[0];
				return new Int16Array((RubyClass) self, data);
			}
			if (argc == 1) {
				int n = Util.acceptNaturalInt32(runtime, args[0]);
				short[] data = new short[n];
				return new Int16Array((RubyClass) self, data);
			}
			if (argc == 2) {
				int n = Util.acceptNaturalInt32(runtime, args[0]);
				short[] data = new short[n];
				java.util.Arrays.fill(data, Util.acceptInt16(runtime, args[1]));
				return new Int16Array((RubyClass) self, data);
			}
			throw runtime.newArgumentError("wrong number of arguments");
		}
		@JRubyMethod(meta = true, name = "of", rest = true)
		public static Int16Array __of(IRubyObject self, IRubyObject[] args) {
			Ruby runtime = self.getRuntime();
			int n = args.length;
			short[] data = new short[n];
			for (int i = 0; i < n; i += 1)
				data[i] = Util.acceptInt16(runtime, args[i]);
			return new Int16Array((RubyClass) self, data);
		}
		final short[] data;
		Int16Array(RubyClass metaClass, short[] data) {
			super(metaClass);
			this.data = data;
			return;
		}
		public java.lang.Object getObject() {
			return data;
		}
		@JRubyMethod(name = "is_a?")
		public RubyBoolean __is$is_a(IRubyObject module) {
			Ruby runtime = getRuntime();
			if (module instanceof RubyModule) {
				if (module == getMetaClass())
					return runtime.getTrue();
				if (module == Gate.getCurrent(runtime).JAVA_LANG_OBJECT_CLASS)
					return runtime.getTrue();
				return runtime.getFalse();
			}
			throw runtime.newTypeError("class or module required");
		}
		@JRubyMethod(name = "clone")
		public Int16Array __clone() {
			return new Int16Array(getMetaClass(), data.clone());
		}
		@JRubyMethod(name = "dup")
		public Int16Array __dup() {
			return new Int16Array(getMetaClass(), data.clone());
		}
		@JRubyMethod(name = "length")
		public Int32 __length() {
			Ruby runtime = getRuntime();
			return new Int32(Gate.getCurrent(runtime).INT32_CLASS, data.length);
		}
		@JRubyMethod(name = "[]")
		public Int16 __get(IRubyObject index) {
			Ruby runtime = getRuntime();
			int i = Util.acceptBoundedInt32(runtime, index, data.length);
			return Util.newInt16(runtime, data[i]);
		}
		@JRubyMethod(name = "[]=")
		public RubyNil __set(IRubyObject index, IRubyObject e) {
			Ruby runtime = getRuntime();
			int i = Util.acceptBoundedInt32(runtime, index, data.length);
			data[i] = Util.acceptInt16(runtime, e);
			return (RubyNil) runtime.getNil();
		}
		@JRubyMethod(name = "copy", required = 4)
		public RubyNil __copy(IRubyObject[] args) {
			Ruby runtime = getRuntime();
			IRubyObject i0 = args[0];
			IRubyObject m1 = args[1];
			IRubyObject i1 = args[2];
			IRubyObject n = args[3];
			if (m1 instanceof Int16Array) {
				Util.arraycopy(runtime, this, Util.acceptNaturalInt32(runtime, i0), (Int16Array) m1, Util.acceptNaturalInt32(runtime, i1), Util.acceptNaturalInt32(runtime, n));
				return (RubyNil) runtime.getNil();
			}
			throw runtime.newTypeError("expected array");
		}
		@JRubyMethod(name = "each")
		public RubyNil __each(Block block) {
			Ruby runtime = getRuntime();
			ThreadContext context = runtime.getCurrentContext();
			int i = 0;
			while (i < data.length) {
				block.call(context, Util.newInt16(runtime, data[i]));
				i += 1;
			}
			return (RubyNil) runtime.getNil();
		}
	}
	public static final class Int32Array extends JavaArray {
		@JRubyMethod(meta = true, name = "~")
		public static Proxy __inv(IRubyObject self) {
			Ruby runtime = self.getRuntime();
			java.lang.Class<?> clazz = Util.fetchJavaClass((RubyClass) self);
			return Gate.getCurrent(runtime).newClassProxy(clazz);
		}
		@JRubyMethod(meta = true, name = "[]", rest = true)
		public static IRubyObject __splat(IRubyObject self, IRubyObject[] args) {
			Ruby runtime = self.getRuntime();
			RubyClass klass = Util.makeOuter(runtime, (RubyClass) self);
			return Util.splat(runtime, klass, args);
		}
		@JRubyMethod(meta = true, name = "new", rest = true)
		public static Int32Array __new(IRubyObject self, IRubyObject[] args) {
			Ruby runtime = self.getRuntime();
			int argc = args.length;
			if (argc == 0) {
				int[] data = new int[0];
				return new Int32Array((RubyClass) self, data);
			}
			if (argc == 1) {
				int n = Util.acceptNaturalInt32(runtime, args[0]);
				int[] data = new int[n];
				return new Int32Array((RubyClass) self, data);
			}
			if (argc == 2) {
				int n = Util.acceptNaturalInt32(runtime, args[0]);
				int[] data = new int[n];
				java.util.Arrays.fill(data, Util.acceptInt32(runtime, args[1]));
				return new Int32Array((RubyClass) self, data);
			}
			throw runtime.newArgumentError("wrong number of arguments");
		}
		@JRubyMethod(meta = true, name = "of", rest = true)
		public static Int32Array __of(IRubyObject self, IRubyObject[] args) {
			Ruby runtime = self.getRuntime();
			int n = args.length;
			int[] data = new int[n];
			for (int i = 0; i < n; i += 1)
				data[i] = Util.acceptInt32(runtime, args[i]);
			return new Int32Array((RubyClass) self, data);
		}
		final int[] data;
		Int32Array(RubyClass metaClass, int[] data) {
			super(metaClass);
			this.data = data;
			return;
		}
		public java.lang.Object getObject() {
			return data;
		}
		@JRubyMethod(name = "is_a?")
		public RubyBoolean __is$is_a(IRubyObject module) {
			Ruby runtime = getRuntime();
			if (module instanceof RubyModule) {
				if (module == getMetaClass())
					return runtime.getTrue();
				if (module == Gate.getCurrent(runtime).JAVA_LANG_OBJECT_CLASS)
					return runtime.getTrue();
				return runtime.getFalse();
			}
			throw runtime.newTypeError("class or module required");
		}
		@JRubyMethod(name = "clone")
		public Int32Array __clone() {
			return new Int32Array(getMetaClass(), data.clone());
		}
		@JRubyMethod(name = "dup")
		public Int32Array __dup() {
			return new Int32Array(getMetaClass(), data.clone());
		}
		@JRubyMethod(name = "length")
		public Int32 __length() {
			Ruby runtime = getRuntime();
			return new Int32(Gate.getCurrent(runtime).INT32_CLASS, data.length);
		}
		@JRubyMethod(name = "[]")
		public Int32 __get(IRubyObject index) {
			Ruby runtime = getRuntime();
			int i = Util.acceptBoundedInt32(runtime, index, data.length);
			return Util.newInt32(runtime, data[i]);
		}
		@JRubyMethod(name = "[]=")
		public RubyNil __set(IRubyObject index, IRubyObject e) {
			Ruby runtime = getRuntime();
			int i = Util.acceptBoundedInt32(runtime, index, data.length);
			data[i] = Util.acceptInt32(runtime, e);
			return (RubyNil) runtime.getNil();
		}
		@JRubyMethod(name = "copy", required = 4)
		public RubyNil __copy(IRubyObject[] args) {
			Ruby runtime = getRuntime();
			IRubyObject i0 = args[0];
			IRubyObject m1 = args[1];
			IRubyObject i1 = args[2];
			IRubyObject n = args[3];
			if (m1 instanceof Int32Array) {
				Util.arraycopy(runtime, this, Util.acceptNaturalInt32(runtime, i0), (Int32Array) m1, Util.acceptNaturalInt32(runtime, i1), Util.acceptNaturalInt32(runtime, n));
				return (RubyNil) runtime.getNil();
			}
			throw runtime.newTypeError("expected array");
		}
		@JRubyMethod(name = "each")
		public RubyNil __each(Block block) {
			Ruby runtime = getRuntime();
			ThreadContext context = runtime.getCurrentContext();
			int i = 0;
			while (i < data.length) {
				block.call(context, Util.newInt32(runtime, data[i]));
				i += 1;
			}
			return (RubyNil) runtime.getNil();
		}
	}
	public static final class Int64Array extends JavaArray {
		@JRubyMethod(meta = true, name = "~")
		public static Proxy __inv(IRubyObject self) {
			Ruby runtime = self.getRuntime();
			java.lang.Class<?> clazz = Util.fetchJavaClass((RubyClass) self);
			return Gate.getCurrent(runtime).newClassProxy(clazz);
		}
		@JRubyMethod(meta = true, name = "[]", rest = true)
		public static IRubyObject __splat(IRubyObject self, IRubyObject[] args) {
			Ruby runtime = self.getRuntime();
			RubyClass klass = Util.makeOuter(runtime, (RubyClass) self);
			return Util.splat(runtime, klass, args);
		}
		@JRubyMethod(meta = true, name = "new", rest = true)
		public static Int64Array __new(IRubyObject self, IRubyObject[] args) {
			Ruby runtime = self.getRuntime();
			int argc = args.length;
			if (argc == 0) {
				long[] data = new long[0];
				return new Int64Array((RubyClass) self, data);
			}
			if (argc == 1) {
				int n = Util.acceptNaturalInt32(runtime, args[0]);
				long[] data = new long[n];
				return new Int64Array((RubyClass) self, data);
			}
			if (argc == 2) {
				int n = Util.acceptNaturalInt32(runtime, args[0]);
				long[] data = new long[n];
				java.util.Arrays.fill(data, Util.acceptInt64(runtime, args[1]));
				return new Int64Array((RubyClass) self, data);
			}
			throw runtime.newArgumentError("wrong number of arguments");
		}
		@JRubyMethod(meta = true, name = "of", rest = true)
		public static Int64Array __of(IRubyObject self, IRubyObject[] args) {
			Ruby runtime = self.getRuntime();
			int n = args.length;
			long[] data = new long[n];
			for (int i = 0; i < n; i += 1)
				data[i] = Util.acceptInt64(runtime, args[i]);
			return new Int64Array((RubyClass) self, data);
		}
		final long[] data;
		Int64Array(RubyClass metaClass, long[] data) {
			super(metaClass);
			this.data = data;
			return;
		}
		public java.lang.Object getObject() {
			return data;
		}
		@JRubyMethod(name = "is_a?")
		public RubyBoolean __is$is_a(IRubyObject module) {
			Ruby runtime = getRuntime();
			if (module instanceof RubyModule) {
				if (module == getMetaClass())
					return runtime.getTrue();
				if (module == Gate.getCurrent(runtime).JAVA_LANG_OBJECT_CLASS)
					return runtime.getTrue();
				return runtime.getFalse();
			}
			throw runtime.newTypeError("class or module required");
		}
		@JRubyMethod(name = "clone")
		public Int64Array __clone() {
			return new Int64Array(getMetaClass(), data.clone());
		}
		@JRubyMethod(name = "dup")
		public Int64Array __dup() {
			return new Int64Array(getMetaClass(), data.clone());
		}
		@JRubyMethod(name = "length")
		public Int32 __length() {
			Ruby runtime = getRuntime();
			return new Int32(Gate.getCurrent(runtime).INT32_CLASS, data.length);
		}
		@JRubyMethod(name = "[]")
		public Int64 __get(IRubyObject index) {
			Ruby runtime = getRuntime();
			int i = Util.acceptBoundedInt32(runtime, index, data.length);
			return Util.newInt64(runtime, data[i]);
		}
		@JRubyMethod(name = "[]=")
		public RubyNil __set(IRubyObject index, IRubyObject e) {
			Ruby runtime = getRuntime();
			int i = Util.acceptBoundedInt32(runtime, index, data.length);
			data[i] = Util.acceptInt64(runtime, e);
			return (RubyNil) runtime.getNil();
		}
		@JRubyMethod(name = "copy", required = 4)
		public RubyNil __copy(IRubyObject[] args) {
			Ruby runtime = getRuntime();
			IRubyObject i0 = args[0];
			IRubyObject m1 = args[1];
			IRubyObject i1 = args[2];
			IRubyObject n = args[3];
			if (m1 instanceof Int64Array) {
				Util.arraycopy(runtime, this, Util.acceptNaturalInt32(runtime, i0), (Int64Array) m1, Util.acceptNaturalInt32(runtime, i1), Util.acceptNaturalInt32(runtime, n));
				return (RubyNil) runtime.getNil();
			}
			throw runtime.newTypeError("expected array");
		}
		@JRubyMethod(name = "each")
		public RubyNil __each(Block block) {
			Ruby runtime = getRuntime();
			ThreadContext context = runtime.getCurrentContext();
			int i = 0;
			while (i < data.length) {
				block.call(context, Util.newInt64(runtime, data[i]));
				i += 1;
			}
			return (RubyNil) runtime.getNil();
		}
	}
	public static final class Float32Array extends JavaArray {
		@JRubyMethod(meta = true, name = "~")
		public static Proxy __inv(IRubyObject self) {
			Ruby runtime = self.getRuntime();
			java.lang.Class<?> clazz = Util.fetchJavaClass((RubyClass) self);
			return Gate.getCurrent(runtime).newClassProxy(clazz);
		}
		@JRubyMethod(meta = true, name = "[]", rest = true)
		public static IRubyObject __splat(IRubyObject self, IRubyObject[] args) {
			Ruby runtime = self.getRuntime();
			RubyClass klass = Util.makeOuter(runtime, (RubyClass) self);
			return Util.splat(runtime, klass, args);
		}
		@JRubyMethod(meta = true, name = "new", rest = true)
		public static Float32Array __new(IRubyObject self, IRubyObject[] args) {
			Ruby runtime = self.getRuntime();
			int argc = args.length;
			if (argc == 0) {
				float[] data = new float[0];
				return new Float32Array((RubyClass) self, data);
			}
			if (argc == 1) {
				int n = Util.acceptNaturalInt32(runtime, args[0]);
				float[] data = new float[n];
				return new Float32Array((RubyClass) self, data);
			}
			if (argc == 2) {
				int n = Util.acceptNaturalInt32(runtime, args[0]);
				float[] data = new float[n];
				java.util.Arrays.fill(data, Util.acceptFloat32(runtime, args[1]));
				return new Float32Array((RubyClass) self, data);
			}
			throw runtime.newArgumentError("wrong number of arguments");
		}
		@JRubyMethod(meta = true, name = "of", rest = true)
		public static Float32Array __of(IRubyObject self, IRubyObject[] args) {
			Ruby runtime = self.getRuntime();
			int n = args.length;
			float[] data = new float[n];
			for (int i = 0; i < n; i += 1)
				data[i] = Util.acceptFloat32(runtime, args[i]);
			return new Float32Array((RubyClass) self, data);
		}
		final float[] data;
		Float32Array(RubyClass metaClass, float[] data) {
			super(metaClass);
			this.data = data;
			return;
		}
		public java.lang.Object getObject() {
			return data;
		}
		@JRubyMethod(name = "is_a?")
		public RubyBoolean __is$is_a(IRubyObject module) {
			Ruby runtime = getRuntime();
			if (module instanceof RubyModule) {
				if (module == getMetaClass())
					return runtime.getTrue();
				if (module == Gate.getCurrent(runtime).JAVA_LANG_OBJECT_CLASS)
					return runtime.getTrue();
				return runtime.getFalse();
			}
			throw runtime.newTypeError("class or module required");
		}
		@JRubyMethod(name = "clone")
		public Float32Array __clone() {
			return new Float32Array(getMetaClass(), data.clone());
		}
		@JRubyMethod(name = "dup")
		public Float32Array __dup() {
			return new Float32Array(getMetaClass(), data.clone());
		}
		@JRubyMethod(name = "length")
		public Int32 __length() {
			Ruby runtime = getRuntime();
			return new Int32(Gate.getCurrent(runtime).INT32_CLASS, data.length);
		}
		@JRubyMethod(name = "[]")
		public Float32 __get(IRubyObject index) {
			Ruby runtime = getRuntime();
			int i = Util.acceptBoundedInt32(runtime, index, data.length);
			return Util.newFloat32(runtime, data[i]);
		}
		@JRubyMethod(name = "[]=")
		public RubyNil __set(IRubyObject index, IRubyObject e) {
			Ruby runtime = getRuntime();
			int i = Util.acceptBoundedInt32(runtime, index, data.length);
			data[i] = Util.acceptFloat32(runtime, e);
			return (RubyNil) runtime.getNil();
		}
		@JRubyMethod(name = "copy", required = 4)
		public RubyNil __copy(IRubyObject[] args) {
			Ruby runtime = getRuntime();
			IRubyObject i0 = args[0];
			IRubyObject m1 = args[1];
			IRubyObject i1 = args[2];
			IRubyObject n = args[3];
			if (m1 instanceof Float32Array) {
				Util.arraycopy(runtime, this, Util.acceptNaturalInt32(runtime, i0), (Float32Array) m1, Util.acceptNaturalInt32(runtime, i1), Util.acceptNaturalInt32(runtime, n));
				return (RubyNil) runtime.getNil();
			}
			throw runtime.newTypeError("expected array");
		}
		@JRubyMethod(name = "each")
		public RubyNil __each(Block block) {
			Ruby runtime = getRuntime();
			ThreadContext context = runtime.getCurrentContext();
			int i = 0;
			while (i < data.length) {
				block.call(context, Util.newFloat32(runtime, data[i]));
				i += 1;
			}
			return (RubyNil) runtime.getNil();
		}
	}
	public static final class Float64Array extends JavaArray {
		@JRubyMethod(meta = true, name = "~")
		public static Proxy __inv(IRubyObject self) {
			Ruby runtime = self.getRuntime();
			java.lang.Class<?> clazz = Util.fetchJavaClass((RubyClass) self);
			return Gate.getCurrent(runtime).newClassProxy(clazz);
		}
		@JRubyMethod(meta = true, name = "[]", rest = true)
		public static IRubyObject __splat(IRubyObject self, IRubyObject[] args) {
			Ruby runtime = self.getRuntime();
			RubyClass klass = Util.makeOuter(runtime, (RubyClass) self);
			return Util.splat(runtime, klass, args);
		}
		@JRubyMethod(meta = true, name = "new", rest = true)
		public static Float64Array __new(IRubyObject self, IRubyObject[] args) {
			Ruby runtime = self.getRuntime();
			int argc = args.length;
			if (argc == 0) {
				double[] data = new double[0];
				return new Float64Array((RubyClass) self, data);
			}
			if (argc == 1) {
				int n = Util.acceptNaturalInt32(runtime, args[0]);
				double[] data = new double[n];
				return new Float64Array((RubyClass) self, data);
			}
			if (argc == 2) {
				int n = Util.acceptNaturalInt32(runtime, args[0]);
				double[] data = new double[n];
				java.util.Arrays.fill(data, Util.acceptFloat64(runtime, args[1]));
				return new Float64Array((RubyClass) self, data);
			}
			throw runtime.newArgumentError("wrong number of arguments");
		}
		@JRubyMethod(meta = true, name = "of", rest = true)
		public static Float64Array __of(IRubyObject self, IRubyObject[] args) {
			Ruby runtime = self.getRuntime();
			int n = args.length;
			double[] data = new double[n];
			for (int i = 0; i < n; i += 1)
				data[i] = Util.acceptFloat64(runtime, args[i]);
			return new Float64Array((RubyClass) self, data);
		}
		final double[] data;
		Float64Array(RubyClass metaClass, double[] data) {
			super(metaClass);
			this.data = data;
			return;
		}
		public java.lang.Object getObject() {
			return data;
		}
		@JRubyMethod(name = "is_a?")
		public RubyBoolean __is$is_a(IRubyObject module) {
			Ruby runtime = getRuntime();
			if (module instanceof RubyModule) {
				if (module == getMetaClass())
					return runtime.getTrue();
				if (module == Gate.getCurrent(runtime).JAVA_LANG_OBJECT_CLASS)
					return runtime.getTrue();
				return runtime.getFalse();
			}
			throw runtime.newTypeError("class or module required");
		}
		@JRubyMethod(name = "clone")
		public Float64Array __clone() {
			return new Float64Array(getMetaClass(), data.clone());
		}
		@JRubyMethod(name = "dup")
		public Float64Array __dup() {
			return new Float64Array(getMetaClass(), data.clone());
		}
		@JRubyMethod(name = "length")
		public Int32 __length() {
			Ruby runtime = getRuntime();
			return new Int32(Gate.getCurrent(runtime).INT32_CLASS, data.length);
		}
		@JRubyMethod(name = "[]")
		public Float64 __get(IRubyObject index) {
			Ruby runtime = getRuntime();
			int i = Util.acceptBoundedInt32(runtime, index, data.length);
			return Util.newFloat64(runtime, data[i]);
		}
		@JRubyMethod(name = "[]=")
		public RubyNil __set(IRubyObject index, IRubyObject e) {
			Ruby runtime = getRuntime();
			int i = Util.acceptBoundedInt32(runtime, index, data.length);
			data[i] = Util.acceptFloat64(runtime, e);
			return (RubyNil) runtime.getNil();
		}
		@JRubyMethod(name = "copy", required = 4)
		public RubyNil __copy(IRubyObject[] args) {
			Ruby runtime = getRuntime();
			IRubyObject i0 = args[0];
			IRubyObject m1 = args[1];
			IRubyObject i1 = args[2];
			IRubyObject n = args[3];
			if (m1 instanceof Float64Array) {
				Util.arraycopy(runtime, this, Util.acceptNaturalInt32(runtime, i0), (Float64Array) m1, Util.acceptNaturalInt32(runtime, i1), Util.acceptNaturalInt32(runtime, n));
				return (RubyNil) runtime.getNil();
			}
			throw runtime.newTypeError("expected array");
		}
		@JRubyMethod(name = "each")
		public RubyNil __each(Block block) {
			Ruby runtime = getRuntime();
			ThreadContext context = runtime.getCurrentContext();
			int i = 0;
			while (i < data.length) {
				block.call(context, Util.newFloat64(runtime, data[i]));
				i += 1;
			}
			return (RubyNil) runtime.getNil();
		}
	}
	public static final class KlassArrayFactoryFactory {
		@JRubyMethod(meta = true, name = "~")
		public static Proxy __inv(IRubyObject self) {
			Ruby runtime = self.getRuntime();
			RubyModule module = Util.fetchRubyModule((RubyModule) self);
			RubyClass klass = Util.makeOuter(runtime, module);
			java.lang.Class<?> clazz = Util.fetchJavaClass(klass);
			return Gate.getCurrent(runtime).newClassProxy(clazz.getComponentType());
		}
		@JRubyMethod(meta = true, name = "[]", rest = true)
		public static IRubyObject __splat(IRubyObject self, IRubyObject[] args) {
			Ruby runtime = self.getRuntime();
			RubyModule module = Util.fetchRubyModule((RubyModule) self);
			RubyClass klass = Util.makeOuter(runtime, module);
			return Util.splat(runtime, klass, args);
		}
		@JRubyMethod(meta = true)
		public static IRubyObject const_missing(IRubyObject self, IRubyObject symbol) {
			Ruby runtime = self.getRuntime();
			java.lang.String name = symbol.asJavaString();
			if (Util.isClassName(name)) {
				RubyModule module = Util.fetchRubyModule((RubyModule) self);
				IRubyObject r = module.getConstant(name);
				if (r instanceof RubyModule)
					return Util.newKlassArrayFactoryFactory(runtime, (RubyModule) r);
				throw runtime.newTypeError("expected class or module");
			}
			throw runtime.newNameError(name, null);
		}
	}
	public static final class RUBY {
		@JRubyMethod(meta = true)
		public static IRubyObject method_missing(IRubyObject self, IRubyObject symbol) {
			Ruby runtime = self.getRuntime();
			java.lang.String name = symbol.asJavaString();
			if (Util.isClassName(name)) {
				RubyModule module = runtime.getModule(name);
				if (module != null)
					return Util.newKlassArrayFactoryFactory(runtime, module);
			}
			throw runtime.newNameError(name, null);
		}
	}
	public static final class VOID {
		@JRubyMethod(meta = true, name = "~")
		public static Proxy __inv(IRubyObject self) {
			Ruby runtime = self.getRuntime();
			return Gate.getCurrent(runtime).newClassProxy(void.class);
		}
	}
	public static final class JAVA {
		@JRubyMethod(meta = true, name = "void")
		public static IRubyObject __void(IRubyObject self) {
			Ruby runtime = self.getRuntime();
			return Gate.getCurrent(runtime).VOID_MODULE;
		}
		@JRubyMethod(meta = true, name = "boolean")
		public static IRubyObject __boolean(IRubyObject self) {
			Ruby runtime = self.getRuntime();
			return Gate.getCurrent(runtime).BOOLEAN_ARRAY_FACTORY_FACTORY;
		}
		@JRubyMethod(meta = true, name = "byte")
		public static IRubyObject __byte(IRubyObject self) {
			Ruby runtime = self.getRuntime();
			return Gate.getCurrent(runtime).BYTE_ARRAY_FACTORY_FACTORY;
		}
		@JRubyMethod(meta = true, name = "char")
		public static IRubyObject __char(IRubyObject self) {
			Ruby runtime = self.getRuntime();
			return Gate.getCurrent(runtime).CHAR_ARRAY_FACTORY_FACTORY;
		}
		@JRubyMethod(meta = true, name = "int16")
		public static IRubyObject __int16(IRubyObject self) {
			Ruby runtime = self.getRuntime();
			return Gate.getCurrent(runtime).INT16_ARRAY_FACTORY_FACTORY;
		}
		@JRubyMethod(meta = true, name = "int32")
		public static IRubyObject __int32(IRubyObject self) {
			Ruby runtime = self.getRuntime();
			return Gate.getCurrent(runtime).INT32_ARRAY_FACTORY_FACTORY;
		}
		@JRubyMethod(meta = true, name = "int64")
		public static IRubyObject __int64(IRubyObject self) {
			Ruby runtime = self.getRuntime();
			return Gate.getCurrent(runtime).INT64_ARRAY_FACTORY_FACTORY;
		}
		@JRubyMethod(meta = true, name = "float32")
		public static IRubyObject __float32(IRubyObject self) {
			Ruby runtime = self.getRuntime();
			return Gate.getCurrent(runtime).FLOAT32_ARRAY_FACTORY_FACTORY;
		}
		@JRubyMethod(meta = true, name = "float64")
		public static IRubyObject __float64(IRubyObject self) {
			Ruby runtime = self.getRuntime();
			return Gate.getCurrent(runtime).FLOAT64_ARRAY_FACTORY_FACTORY;
		}
		@JRubyMethod(meta = true, name = "ARRAYCOPY", required = 5)
		public static IRubyObject __arraycopy(IRubyObject self, IRubyObject[] args) {
			Ruby runtime = self.getRuntime();
			ThreadContext context = runtime.getCurrentContext();
			IRubyObject m0 = args[0];
			IRubyObject i0 = args[1];
			IRubyObject m1 = args[2];
			IRubyObject i1 = args[3];
			IRubyObject n = args[4];
			return m0.callMethod(context, "copy", new IRubyObject[] { i0, m1, i1, n });
		}
		@JRubyMethod(meta = true, name = "ruby")
		public static IRubyObject __ruby(IRubyObject self) {
			Ruby runtime = self.getRuntime();
			return Gate.getCurrent(runtime).RUBY_MODULE;
		}
		@JRubyMethod(meta = true)
		public static IRubyObject method_missing(IRubyObject self, IRubyObject symbol) {
			Ruby runtime = self.getRuntime();
			java.lang.String name = symbol.asJavaString();
			if (Util.isPackageName(name))
				return Util.newPackage(runtime, name);
			throw runtime.newNoMethodError(name, null, null);
		}
	}
	@Override
	public boolean basicLoad(Ruby runtime) {
		runtime.getKernel().defineAnnotatedMethods(KernelMixin.class);
		runtime.getString().defineAnnotatedMethods(StringMixin.class);
		Gate gate = new Gate(runtime);
		Gate.setCurrent(runtime, gate);
		runtime.defineVariable(new ErrorInfoGlobalVariable(runtime, "$error", null), org.jruby.internal.runtime.GlobalVariable.Scope.THREAD);
		return true;
	}
}
