#include <ruby.h>
#include <math.h>
static VALUE r_JAVA;
static ID NEW_ID, COPY_ID;
static ID INNER_ID, OUTER_ID;
static ID MODULE_ID;
static ID TO_J_ID;
static ID JAVA_ID, LANG_ID;
static ID OBJECT_ID, THROWABLE_ID;
static int32_t
__div_int32(int32_t x, int32_t y)
{
	if (y == 0)
		rb_raise(rb_eZeroDivError, "divided by 0");
	return x / y;
}
static int32_t
__mod_int32(int32_t x, int32_t y)
{
	if (y == 0)
		rb_raise(rb_eZeroDivError, "divided by 0");
	return x % y;
}
static int64_t
__div_int64(int64_t x, int64_t y)
{
	if (y == 0)
		rb_raise(rb_eZeroDivError, "divided by 0");
	return x / y;
}
static int64_t
__mod_int64(int64_t x, int64_t y)
{
	if (y == 0)
		rb_raise(rb_eZeroDivError, "divided by 0");
	return x % y;
}
static float
__div_float32(float x, float y)
{
	return x / y;
}
static float
__mod_float32(float x, float y)
{
	return fmodf(x, y);
}
static double
__div_float64(double x, double y)
{
	return x / y;
}
static double
__mod_float64(double x, double y)
{
	return fmod(x, y);
}
static int32_t
__hash_int32(int32_t value)
{
	return value;
}
static int32_t
__hash_int64(int64_t value)
{
	return (int32_t) value ^ (int32_t) (value >> 32);
}
static int32_t
__hash_float32(float value)
{
	union {
		int32_t i32;
		float f32;
	} u;
	u.f32 = value;
	return __hash_int32(u.i32);
}
static int32_t
__hash_float64(double value)
{
	union {
		int64_t i64;
		double f64;
	} u;
	u.f64 = value;
	return __hash_int64(u.i64);
}
static VALUE
__int32_to_i(int32_t value)
{
	return LONG2NUM((long) value);
}
static VALUE
__int32_to_f(int32_t value)
{
	return DBL2NUM((double) value);
}
static VALUE
__int32_to_s(int32_t value)
{
	char s[11 + 1];
	snprintf(s, 11 + 1, "%d", value);
	return rb_usascii_str_new2(s);
}
static VALUE
__int32_to_hex(int32_t value)
{
	char s[010 + 1];
	int i = 010;
	s[i] = 0;
	while (1) {
		i -= 1;
		int c = (int32_t) value & 0x0F;
		s[i] = (char) (c < 10 ? c + 0x30 : c + 0x41 - 10);
		if (i == 0)
			break;
		value >>= 4;
	}
	return rb_usascii_str_new2(s);
}
static VALUE
__int32_chr(int32_t value)
{
	if (value < 0x00)
		rb_raise(rb_eRangeError, "out of range");
	if (value <= 0x7F) {
		char s = (char) value;
		return rb_usascii_str_new(&s, 1);
	}
	if (value <= 0xFF) {
		char s = (char) value;
		return rb_str_new(&s, 1);
	}
	rb_raise(rb_eRangeError, "out of range");
}
static VALUE
__int64_to_i(int64_t value)
{
	return LONG2NUM((long) value);
}
static VALUE
__int64_to_f(int64_t value)
{
	return DBL2NUM((double) value);
}
static VALUE
__int64_to_s(int64_t value)
{
	char s[20 + 1];
	snprintf(s, 20 + 1, "%ld", value);
	return rb_usascii_str_new2(s);
}
static VALUE
__int64_to_hex(int64_t value)
{
	char s[020 + 1];
	int i = 020;
	s[i] = 0;
	while (1) {
		i -= 1;
		int c = (int32_t) value & 0x0F;
		s[i] = (char) (c < 10 ? c + 0x30 : c + 0x41 - 10);
		if (i == 0)
			break;
		value >>= 4;
	}
	return rb_usascii_str_new2(s);
}
static VALUE
__float32_to_i(float value)
{
	if (isnan(value))
		rb_raise(rb_eFloatDomainError, "NaN");
	if (isinf(value))
		rb_raise(rb_eFloatDomainError, value < 0 ? "-Infinity" : "Infinity");
	if (value < FIXNUM_MIN || value >= FIXNUM_MAX + 1)
		return rb_dbl2big((double) value);
	return LONG2FIX((long) value);
}
static VALUE
__float32_to_f(float value)
{
	return DBL2NUM((double) value);
}
static VALUE
__float32_to_s(float value)
{
	if (isnan(value))
		return rb_usascii_str_new2("NaN");
	if (value == INFINITY)
		return rb_usascii_str_new2("Infinity");
	if (value == -INFINITY)
		return rb_usascii_str_new2("-Infinity");
	char s[15 + 1];
	snprintf(s, 15 + 1, "%.9G", value);
	return rb_usascii_str_new2(s);
}
static VALUE
__float64_to_i(double value)
{
	if (isnan(value))
		rb_raise(rb_eFloatDomainError, "NaN");
	if (isinf(value))
		rb_raise(rb_eFloatDomainError, value < 0 ? "-Infinity" : "Infinity");
	if (value < FIXNUM_MIN || value >= FIXNUM_MAX + 1)
		return rb_dbl2big(value);
	return LONG2FIX((long) value);
}
static VALUE
__float64_to_f(double value)
{
	return DBL2NUM((double) value);
}
static VALUE
__float64_to_s(double value)
{
	if (isnan(value))
		return rb_usascii_str_new2("NaN");
	if (value == INFINITY)
		return rb_usascii_str_new2("Infinity");
	if (value == -INFINITY)
		return rb_usascii_str_new2("-Infinity");
	char s[24 + 1];
	snprintf(s, 24 + 1, "%.17G", value);
	return rb_usascii_str_new2(s);
}
static VALUE r_INTEGER_X8000000000000000;
static VALUE r_INTEGER_XFFFFFFFFFFFFFFFF;
static VALUE r_INTEGER_MX8000000000000000;
static int64_t
__Bignum_long_value(VALUE o)
{
	o = rb_big_plus(r_INTEGER_X8000000000000000, o);
	o = rb_big_and(r_INTEGER_XFFFFFFFFFFFFFFFF, o);
	o = rb_big_plus(r_INTEGER_MX8000000000000000, o);
	return NUM2LONG(o);
}
struct Byte {
	int8_t value;
};
typedef struct Byte Byte;
static VALUE r_Byte;
struct Char {
	uint16_t value;
};
typedef struct Char Char;
static VALUE r_Char;
struct Int16 {
	int16_t value;
};
typedef struct Int16 Int16;
static VALUE r_Int16;
struct Int32 {
	int32_t value;
};
typedef struct Int32 Int32;
static VALUE r_Int32;
struct Int64 {
	int64_t value;
};
typedef struct Int64 Int64;
static VALUE r_Int64;
struct Float32 {
	float value;
};
typedef struct Float32 Float32;
static VALUE r_Float32;
struct Float64 {
	double value;
};
typedef struct Float64 Float64;
static VALUE r_Float64;
static VALUE
__allocate_Byte(int8_t value)
{
	Byte *p = ALLOC(Byte);
	if (p == NULL)
		rb_raise(rb_eNoMemError, "NoMemoryError");
	p->value = value;
	return Data_Wrap_Struct(r_Byte, NULL, NULL, p);
}
static VALUE
__allocate_Char(uint16_t value)
{
	Char *p = ALLOC(Char);
	if (p == NULL)
		rb_raise(rb_eNoMemError, "NoMemoryError");
	p->value = value;
	return Data_Wrap_Struct(r_Char, NULL, NULL, p);
}
static VALUE
__allocate_Int16(int16_t value)
{
	Int16 *p = ALLOC(Int16);
	if (p == NULL)
		rb_raise(rb_eNoMemError, "NoMemoryError");
	p->value = value;
	return Data_Wrap_Struct(r_Int16, NULL, NULL, p);
}
static VALUE
__allocate_Int32(int32_t value)
{
	Int32 *p = ALLOC(Int32);
	if (p == NULL)
		rb_raise(rb_eNoMemError, "NoMemoryError");
	p->value = value;
	return Data_Wrap_Struct(r_Int32, NULL, NULL, p);
}
static VALUE
__allocate_Int64(int64_t value)
{
	Int64 *p = ALLOC(Int64);
	if (p == NULL)
		rb_raise(rb_eNoMemError, "NoMemoryError");
	p->value = value;
	return Data_Wrap_Struct(r_Int64, NULL, NULL, p);
}
static VALUE
__allocate_Float32(float value)
{
	Float32 *p = ALLOC(Float32);
	if (p == NULL)
		rb_raise(rb_eNoMemError, "NoMemoryError");
	p->value = value;
	return Data_Wrap_Struct(r_Float32, NULL, NULL, p);
}
static VALUE
__allocate_Float64(double value)
{
	Float64 *p = ALLOC(Float64);
	if (p == NULL)
		rb_raise(rb_eNoMemError, "NoMemoryError");
	p->value = value;
	return Data_Wrap_Struct(r_Float64, NULL, NULL, p);
}
static VALUE r_INT32_0;
static VALUE r_INT32_1;
static VALUE r_INT32_M1;
static int8_t
__accept_boolean(VALUE o)
{
	switch (TYPE(o)) {
	case T_TRUE:
		return 1;
	case T_FALSE:
		return 0;
	}
	rb_raise(rb_eTypeError, "expected boolean");
}
static int8_t
__accept_byte(VALUE o)
{
	switch (TYPE(o)) {
	case T_FIXNUM:
		return (int8_t) FIX2LONG(o);
	case T_BIGNUM:
		return (int8_t) __Bignum_long_value(o);
	case T_DATA:
		if (RBASIC(o)->klass == r_Byte) {
			Byte *p;
			Data_Get_Struct(o, Byte, p);
			return p->value;
		}
	}
	rb_raise(rb_eTypeError, "expected byte");
}
static uint16_t
__accept_char(VALUE o)
{
	switch (TYPE(o)) {
	case T_FIXNUM:
		return (uint16_t) FIX2LONG(o);
	case T_BIGNUM:
		return (uint16_t) __Bignum_long_value(o);
	case T_DATA:
		if (RBASIC(o)->klass == r_Char) {
			Char *p;
			Data_Get_Struct(o, Char, p);
			return p->value;
		}
	}
	rb_raise(rb_eTypeError, "expected char");
}
static int16_t
__accept_int16(VALUE o)
{
	switch (TYPE(o)) {
	case T_FIXNUM:
		return (int16_t) FIX2LONG(o);
	case T_BIGNUM:
		return (int16_t) __Bignum_long_value(o);
	case T_DATA:
		if (RBASIC(o)->klass == r_Int16) {
			Int16 *p;
			Data_Get_Struct(o, Int16, p);
			return p->value;
		}
	}
	rb_raise(rb_eTypeError, "expected int16");
}
static int32_t
__accept_int32(VALUE o)
{
	switch (TYPE(o)) {
	case T_FIXNUM:
		return (int32_t) FIX2LONG(o);
	case T_BIGNUM:
		return (int32_t) __Bignum_long_value(o);
	case T_DATA:
		if (RBASIC(o)->klass == r_Byte) {
			Byte *p;
			Data_Get_Struct(o, Byte, p);
			return p->value;
		}
		if (RBASIC(o)->klass == r_Char) {
			Char *p;
			Data_Get_Struct(o, Char, p);
			return p->value;
		}
		if (RBASIC(o)->klass == r_Int16) {
			Int16 *p;
			Data_Get_Struct(o, Int16, p);
			return p->value;
		}
		if (RBASIC(o)->klass == r_Int32) {
			Int32 *p;
			Data_Get_Struct(o, Int32, p);
			return p->value;
		}
	}
	rb_raise(rb_eTypeError, "expected int32");
}
static int64_t
__accept_int64(VALUE o)
{
	switch (TYPE(o)) {
	case T_FIXNUM:
		return (int64_t) FIX2LONG(o);
	case T_BIGNUM:
		return (int64_t) __Bignum_long_value(o);
	case T_DATA:
		if (RBASIC(o)->klass == r_Int64) {
			Int64 *p;
			Data_Get_Struct(o, Int64, p);
			return p->value;
		}
	}
	rb_raise(rb_eTypeError, "expected int64");
}
static float
__accept_float32(VALUE o)
{
	switch (TYPE(o)) {
	case T_FIXNUM:
		return (float) FIX2LONG(o);
	case T_BIGNUM:
		return (float) NUM2DBL(o);
	case T_FLOAT:
		return (float) NUM2DBL(o);
	case T_DATA:
		if (RBASIC(o)->klass == r_Float32) {
			Float32 *p;
			Data_Get_Struct(o, Float32, p);
			return p->value;
		}
	}
	rb_raise(rb_eTypeError, "expected float32");
}
static double
__accept_float64(VALUE o)
{
	switch (TYPE(o)) {
	case T_FIXNUM:
		return (double) FIX2LONG(o);
	case T_BIGNUM:
		return (double) NUM2DBL(o);
	case T_FLOAT:
		return (double) NUM2DBL(o);
	case T_DATA:
		if (RBASIC(o)->klass == r_Float64) {
			Float64 *p;
			Data_Get_Struct(o, Float64, p);
			return p->value;
		}
	}
	rb_raise(rb_eTypeError, "expected float64");
}
static int32_t
__accept_bounded_int32(VALUE o, int32_t n)
{
	int32_t i = __accept_int32(o);
	if (i < 0 || i >= n)
		rb_raise(rb_eArgError, "index out of bounds");
	return i;
}
static int32_t
__accept_natural_int32(VALUE o)
{
	int32_t i = __accept_int32(o);
	if (i < 0)
		rb_raise(rb_eArgError, "out of range");
	return i;
}
static void
__assert_class(VALUE o)
{
	if (TYPE(o) != T_CLASS)
		rb_raise(rb_eScriptError, "ScriptError");
	return;
}
static void
__assert_module(VALUE o)
{
	if (TYPE(o) != T_MODULE && TYPE(o) != T_CLASS)
		rb_raise(rb_eScriptError, "ScriptError");
	return;
}
static VALUE
r_Byte_new(VALUE self, VALUE o)
{
	int8_t value = __accept_byte(o);
	return __allocate_Byte(value);
}
static VALUE
r_Byte_equals(VALUE self, VALUE o)
{
	Byte *p;
	Data_Get_Struct(self, Byte, p);
	if (TYPE(o) == T_DATA) {
		if (RBASIC(o)->klass == r_Byte) {
			Byte *op;
			Data_Get_Struct(o, Byte, op);
			if (p->value == op->value)
				return Qtrue;
		}
	}
	return Qfalse;
}
static VALUE
r_Byte_hashCode(VALUE self)
{
	Byte *p;
	Data_Get_Struct(self, Byte, p);
	return __allocate_Int32(__hash_int32(p->value));
}
static VALUE
r_Byte_toString(VALUE self)
{
	Byte *p;
	Data_Get_Struct(self, Byte, p);
	return rb_funcall(__int32_to_s(p->value), TO_J_ID, 0);
}
static VALUE
r_Byte_hash(VALUE self)
{
	Byte *p;
	Data_Get_Struct(self, Byte, p);
	return LONG2NUM((long) __hash_int32(p->value));
}
static VALUE
r_Byte_to_s(VALUE self)
{
	Byte *p;
	Data_Get_Struct(self, Byte, p);
	return __int32_to_s(p->value);
}
static VALUE
r_Byte_to_byte(VALUE self)
{
	return self;
}
static VALUE
r_Byte_to_char(VALUE self)
{
	Byte *p;
	Data_Get_Struct(self, Byte, p);
	return __allocate_Char((uint16_t) p->value);
}
static VALUE
r_Byte_to_int16(VALUE self)
{
	Byte *p;
	Data_Get_Struct(self, Byte, p);
	return __allocate_Int16((int16_t) p->value);
}
static VALUE
r_Byte_to_int32(VALUE self)
{
	Byte *p;
	Data_Get_Struct(self, Byte, p);
	return __allocate_Int32((int32_t) p->value);
}
static VALUE
r_Byte_to_int64(VALUE self)
{
	Byte *p;
	Data_Get_Struct(self, Byte, p);
	return __allocate_Int64((int64_t) p->value);
}
static VALUE
r_Byte_to_float32(VALUE self)
{
	Byte *p;
	Data_Get_Struct(self, Byte, p);
	return __allocate_Float32((float) p->value);
}
static VALUE
r_Byte_to_float64(VALUE self)
{
	Byte *p;
	Data_Get_Struct(self, Byte, p);
	return __allocate_Float64((double) p->value);
}
static VALUE
r_Byte_to_i(VALUE self)
{
	Byte *p;
	Data_Get_Struct(self, Byte, p);
	return __int32_to_i(p->value);
}
static VALUE
r_Byte_to_f(VALUE self)
{
	Byte *p;
	Data_Get_Struct(self, Byte, p);
	return __int32_to_f(p->value);
}
static VALUE
r_Byte_eq(VALUE self, VALUE o)
{
	Byte *p;
	Data_Get_Struct(self, Byte, p);
	int32_t v = __accept_int32(o);
	return p->value == v ? Qtrue : Qfalse;
}
static VALUE
r_Byte_ne(VALUE self, VALUE o)
{
	Byte *p;
	Data_Get_Struct(self, Byte, p);
	int32_t v = __accept_int32(o);
	return p->value != v ? Qtrue : Qfalse;
}
static VALUE
r_Byte_pos(VALUE self)
{
	Byte *p;
	Data_Get_Struct(self, Byte, p);
	return __allocate_Int32(+p->value);
}
static VALUE
r_Byte_neg(VALUE self)
{
	Byte *p;
	Data_Get_Struct(self, Byte, p);
	return __allocate_Int32(-p->value);
}
static VALUE
r_Byte_mul(VALUE self, VALUE o)
{
	Byte *p;
	Data_Get_Struct(self, Byte, p);
	int32_t v = __accept_int32(o);
	return __allocate_Int32(p->value * v);
}
static VALUE
r_Byte_div(VALUE self, VALUE o)
{
	Byte *p;
	Data_Get_Struct(self, Byte, p);
	int32_t v = __accept_int32(o);
	return __allocate_Int32(__div_int32(p->value, v));
}
static VALUE
r_Byte_mod(VALUE self, VALUE o)
{
	Byte *p;
	Data_Get_Struct(self, Byte, p);
	int32_t v = __accept_int32(o);
	return __allocate_Int32(__mod_int32(p->value, v));
}
static VALUE
r_Byte_add(VALUE self, VALUE o)
{
	Byte *p;
	Data_Get_Struct(self, Byte, p);
	int32_t v = __accept_int32(o);
	return __allocate_Int32(p->value + v);
}
static VALUE
r_Byte_sub(VALUE self, VALUE o)
{
	Byte *p;
	Data_Get_Struct(self, Byte, p);
	int32_t v = __accept_int32(o);
	return __allocate_Int32(p->value - v);
}
static VALUE
r_Byte_cmp(VALUE self, VALUE o)
{
	Byte *p;
	Data_Get_Struct(self, Byte, p);
	int32_t v = __accept_int32(o);
	if (p->value < v)
		return r_INT32_M1;
	if (p->value > v)
		return r_INT32_1;
	return r_INT32_0;
}
static VALUE
r_Byte_lt(VALUE self, VALUE o)
{
	Byte *p;
	Data_Get_Struct(self, Byte, p);
	int32_t v = __accept_int32(o);
	return p->value < v ? Qtrue : Qfalse;
}
static VALUE
r_Byte_le(VALUE self, VALUE o)
{
	Byte *p;
	Data_Get_Struct(self, Byte, p);
	int32_t v = __accept_int32(o);
	return p->value <= v ? Qtrue : Qfalse;
}
static VALUE
r_Byte_ge(VALUE self, VALUE o)
{
	Byte *p;
	Data_Get_Struct(self, Byte, p);
	int32_t v = __accept_int32(o);
	return p->value >= v ? Qtrue : Qfalse;
}
static VALUE
r_Byte_gt(VALUE self, VALUE o)
{
	Byte *p;
	Data_Get_Struct(self, Byte, p);
	int32_t v = __accept_int32(o);
	return p->value > v ? Qtrue : Qfalse;
}
static VALUE
r_Byte_inv(VALUE self)
{
	Byte *p;
	Data_Get_Struct(self, Byte, p);
	return __allocate_Int32(~p->value);
}
static VALUE
r_Byte_shl(VALUE self, VALUE o)
{
	Byte *p;
	Data_Get_Struct(self, Byte, p);
	int32_t d = __accept_int32(o);
	return __allocate_Int32(p->value << d);
}
static VALUE
r_Byte_shr(VALUE self, VALUE o)
{
	Byte *p;
	Data_Get_Struct(self, Byte, p);
	int32_t d = __accept_int32(o);
	return __allocate_Int32(p->value >> d);
}
static VALUE
r_Byte_ushr(VALUE self, VALUE o)
{
	Byte *p;
	Data_Get_Struct(self, Byte, p);
	uint32_t u = (uint32_t) p->value;
	int32_t d = __accept_int32(o);
	return __allocate_Int32((int32_t) (u >> d));
}
static VALUE
r_Byte_and(VALUE self, VALUE o)
{
	Byte *p;
	Data_Get_Struct(self, Byte, p);
	int32_t v = __accept_int32(o);
	return __allocate_Int32(p->value & v);
}
static VALUE
r_Byte_xor(VALUE self, VALUE o)
{
	Byte *p;
	Data_Get_Struct(self, Byte, p);
	int32_t v = __accept_int32(o);
	return __allocate_Int32(p->value ^ v);
}
static VALUE
r_Byte_or(VALUE self, VALUE o)
{
	Byte *p;
	Data_Get_Struct(self, Byte, p);
	int32_t v = __accept_int32(o);
	return __allocate_Int32(p->value | v);
}
static VALUE
r_Byte_is$zero(VALUE self)
{
	Byte *p;
	Data_Get_Struct(self, Byte, p);
	return p->value == 0 ? Qtrue : Qfalse;
}
static VALUE
r_Char_new(VALUE self, VALUE o)
{
	uint16_t value = __accept_char(o);
	return __allocate_Char(value);
}
static VALUE
r_Char_equals(VALUE self, VALUE o)
{
	Char *p;
	Data_Get_Struct(self, Char, p);
	if (TYPE(o) == T_DATA) {
		if (RBASIC(o)->klass == r_Char) {
			Char *op;
			Data_Get_Struct(o, Char, op);
			if (p->value == op->value)
				return Qtrue;
		}
	}
	return Qfalse;
}
static VALUE
r_Char_hashCode(VALUE self)
{
	Char *p;
	Data_Get_Struct(self, Char, p);
	return __allocate_Int32(__hash_int32(p->value));
}
static VALUE
r_Char_toString(VALUE self)
{
	Char *p;
	Data_Get_Struct(self, Char, p);
	return rb_funcall(__int32_to_s(p->value), TO_J_ID, 0);
}
static VALUE
r_Char_hash(VALUE self)
{
	Char *p;
	Data_Get_Struct(self, Char, p);
	return LONG2NUM((long) __hash_int32(p->value));
}
static VALUE
r_Char_to_s(VALUE self)
{
	Char *p;
	Data_Get_Struct(self, Char, p);
	return __int32_to_s(p->value);
}
static VALUE
r_Char_to_byte(VALUE self)
{
	Char *p;
	Data_Get_Struct(self, Char, p);
	return __allocate_Byte((int8_t) p->value);
}
static VALUE
r_Char_to_char(VALUE self)
{
	return self;
}
static VALUE
r_Char_to_int16(VALUE self)
{
	Char *p;
	Data_Get_Struct(self, Char, p);
	return __allocate_Int16((int16_t) p->value);
}
static VALUE
r_Char_to_int32(VALUE self)
{
	Char *p;
	Data_Get_Struct(self, Char, p);
	return __allocate_Int32((int32_t) p->value);
}
static VALUE
r_Char_to_int64(VALUE self)
{
	Char *p;
	Data_Get_Struct(self, Char, p);
	return __allocate_Int64((int64_t) p->value);
}
static VALUE
r_Char_to_float32(VALUE self)
{
	Char *p;
	Data_Get_Struct(self, Char, p);
	return __allocate_Float32((float) p->value);
}
static VALUE
r_Char_to_float64(VALUE self)
{
	Char *p;
	Data_Get_Struct(self, Char, p);
	return __allocate_Float64((double) p->value);
}
static VALUE
r_Char_to_i(VALUE self)
{
	Char *p;
	Data_Get_Struct(self, Char, p);
	return __int32_to_i(p->value);
}
static VALUE
r_Char_to_f(VALUE self)
{
	Char *p;
	Data_Get_Struct(self, Char, p);
	return __int32_to_f(p->value);
}
static VALUE
r_Char_eq(VALUE self, VALUE o)
{
	Char *p;
	Data_Get_Struct(self, Char, p);
	int32_t v = __accept_int32(o);
	return p->value == v ? Qtrue : Qfalse;
}
static VALUE
r_Char_ne(VALUE self, VALUE o)
{
	Char *p;
	Data_Get_Struct(self, Char, p);
	int32_t v = __accept_int32(o);
	return p->value != v ? Qtrue : Qfalse;
}
static VALUE
r_Char_pos(VALUE self)
{
	Char *p;
	Data_Get_Struct(self, Char, p);
	return __allocate_Int32(+p->value);
}
static VALUE
r_Char_neg(VALUE self)
{
	Char *p;
	Data_Get_Struct(self, Char, p);
	return __allocate_Int32(-p->value);
}
static VALUE
r_Char_mul(VALUE self, VALUE o)
{
	Char *p;
	Data_Get_Struct(self, Char, p);
	int32_t v = __accept_int32(o);
	return __allocate_Int32(p->value * v);
}
static VALUE
r_Char_div(VALUE self, VALUE o)
{
	Char *p;
	Data_Get_Struct(self, Char, p);
	int32_t v = __accept_int32(o);
	return __allocate_Int32(__div_int32(p->value, v));
}
static VALUE
r_Char_mod(VALUE self, VALUE o)
{
	Char *p;
	Data_Get_Struct(self, Char, p);
	int32_t v = __accept_int32(o);
	return __allocate_Int32(__mod_int32(p->value, v));
}
static VALUE
r_Char_add(VALUE self, VALUE o)
{
	Char *p;
	Data_Get_Struct(self, Char, p);
	int32_t v = __accept_int32(o);
	return __allocate_Int32(p->value + v);
}
static VALUE
r_Char_sub(VALUE self, VALUE o)
{
	Char *p;
	Data_Get_Struct(self, Char, p);
	int32_t v = __accept_int32(o);
	return __allocate_Int32(p->value - v);
}
static VALUE
r_Char_cmp(VALUE self, VALUE o)
{
	Char *p;
	Data_Get_Struct(self, Char, p);
	int32_t v = __accept_int32(o);
	if (p->value < v)
		return r_INT32_M1;
	if (p->value > v)
		return r_INT32_1;
	return r_INT32_0;
}
static VALUE
r_Char_lt(VALUE self, VALUE o)
{
	Char *p;
	Data_Get_Struct(self, Char, p);
	int32_t v = __accept_int32(o);
	return p->value < v ? Qtrue : Qfalse;
}
static VALUE
r_Char_le(VALUE self, VALUE o)
{
	Char *p;
	Data_Get_Struct(self, Char, p);
	int32_t v = __accept_int32(o);
	return p->value <= v ? Qtrue : Qfalse;
}
static VALUE
r_Char_ge(VALUE self, VALUE o)
{
	Char *p;
	Data_Get_Struct(self, Char, p);
	int32_t v = __accept_int32(o);
	return p->value >= v ? Qtrue : Qfalse;
}
static VALUE
r_Char_gt(VALUE self, VALUE o)
{
	Char *p;
	Data_Get_Struct(self, Char, p);
	int32_t v = __accept_int32(o);
	return p->value > v ? Qtrue : Qfalse;
}
static VALUE
r_Char_inv(VALUE self)
{
	Char *p;
	Data_Get_Struct(self, Char, p);
	return __allocate_Int32(~p->value);
}
static VALUE
r_Char_shl(VALUE self, VALUE o)
{
	Char *p;
	Data_Get_Struct(self, Char, p);
	int32_t d = __accept_int32(o);
	return __allocate_Int32(p->value << d);
}
static VALUE
r_Char_shr(VALUE self, VALUE o)
{
	Char *p;
	Data_Get_Struct(self, Char, p);
	int32_t d = __accept_int32(o);
	return __allocate_Int32(p->value >> d);
}
static VALUE
r_Char_ushr(VALUE self, VALUE o)
{
	Char *p;
	Data_Get_Struct(self, Char, p);
	uint32_t u = (uint32_t) p->value;
	int32_t d = __accept_int32(o);
	return __allocate_Int32((int32_t) (u >> d));
}
static VALUE
r_Char_and(VALUE self, VALUE o)
{
	Char *p;
	Data_Get_Struct(self, Char, p);
	int32_t v = __accept_int32(o);
	return __allocate_Int32(p->value & v);
}
static VALUE
r_Char_xor(VALUE self, VALUE o)
{
	Char *p;
	Data_Get_Struct(self, Char, p);
	int32_t v = __accept_int32(o);
	return __allocate_Int32(p->value ^ v);
}
static VALUE
r_Char_or(VALUE self, VALUE o)
{
	Char *p;
	Data_Get_Struct(self, Char, p);
	int32_t v = __accept_int32(o);
	return __allocate_Int32(p->value | v);
}
static VALUE
r_Char_is$zero(VALUE self)
{
	Char *p;
	Data_Get_Struct(self, Char, p);
	return p->value == 0 ? Qtrue : Qfalse;
}
static VALUE
r_Int16_new(VALUE self, VALUE o)
{
	int16_t value = __accept_int16(o);
	return __allocate_Int16(value);
}
static VALUE
r_Int16_equals(VALUE self, VALUE o)
{
	Int16 *p;
	Data_Get_Struct(self, Int16, p);
	if (TYPE(o) == T_DATA) {
		if (RBASIC(o)->klass == r_Int16) {
			Int16 *op;
			Data_Get_Struct(o, Int16, op);
			if (p->value == op->value)
				return Qtrue;
		}
	}
	return Qfalse;
}
static VALUE
r_Int16_hashCode(VALUE self)
{
	Int16 *p;
	Data_Get_Struct(self, Int16, p);
	return __allocate_Int32(__hash_int32(p->value));
}
static VALUE
r_Int16_toString(VALUE self)
{
	Int16 *p;
	Data_Get_Struct(self, Int16, p);
	return rb_funcall(__int32_to_s(p->value), TO_J_ID, 0);
}
static VALUE
r_Int16_hash(VALUE self)
{
	Int16 *p;
	Data_Get_Struct(self, Int16, p);
	return LONG2NUM((long) __hash_int32(p->value));
}
static VALUE
r_Int16_to_s(VALUE self)
{
	Int16 *p;
	Data_Get_Struct(self, Int16, p);
	return __int32_to_s(p->value);
}
static VALUE
r_Int16_to_byte(VALUE self)
{
	Int16 *p;
	Data_Get_Struct(self, Int16, p);
	return __allocate_Byte((int8_t) p->value);
}
static VALUE
r_Int16_to_char(VALUE self)
{
	Int16 *p;
	Data_Get_Struct(self, Int16, p);
	return __allocate_Char((uint16_t) p->value);
}
static VALUE
r_Int16_to_int16(VALUE self)
{
	return self;
}
static VALUE
r_Int16_to_int32(VALUE self)
{
	Int16 *p;
	Data_Get_Struct(self, Int16, p);
	return __allocate_Int32((int32_t) p->value);
}
static VALUE
r_Int16_to_int64(VALUE self)
{
	Int16 *p;
	Data_Get_Struct(self, Int16, p);
	return __allocate_Int64((int64_t) p->value);
}
static VALUE
r_Int16_to_float32(VALUE self)
{
	Int16 *p;
	Data_Get_Struct(self, Int16, p);
	return __allocate_Float32((float) p->value);
}
static VALUE
r_Int16_to_float64(VALUE self)
{
	Int16 *p;
	Data_Get_Struct(self, Int16, p);
	return __allocate_Float64((double) p->value);
}
static VALUE
r_Int16_to_i(VALUE self)
{
	Int16 *p;
	Data_Get_Struct(self, Int16, p);
	return __int32_to_i(p->value);
}
static VALUE
r_Int16_to_f(VALUE self)
{
	Int16 *p;
	Data_Get_Struct(self, Int16, p);
	return __int32_to_f(p->value);
}
static VALUE
r_Int16_eq(VALUE self, VALUE o)
{
	Int16 *p;
	Data_Get_Struct(self, Int16, p);
	int32_t v = __accept_int32(o);
	return p->value == v ? Qtrue : Qfalse;
}
static VALUE
r_Int16_ne(VALUE self, VALUE o)
{
	Int16 *p;
	Data_Get_Struct(self, Int16, p);
	int32_t v = __accept_int32(o);
	return p->value != v ? Qtrue : Qfalse;
}
static VALUE
r_Int16_pos(VALUE self)
{
	Int16 *p;
	Data_Get_Struct(self, Int16, p);
	return __allocate_Int32(+p->value);
}
static VALUE
r_Int16_neg(VALUE self)
{
	Int16 *p;
	Data_Get_Struct(self, Int16, p);
	return __allocate_Int32(-p->value);
}
static VALUE
r_Int16_mul(VALUE self, VALUE o)
{
	Int16 *p;
	Data_Get_Struct(self, Int16, p);
	int32_t v = __accept_int32(o);
	return __allocate_Int32(p->value * v);
}
static VALUE
r_Int16_div(VALUE self, VALUE o)
{
	Int16 *p;
	Data_Get_Struct(self, Int16, p);
	int32_t v = __accept_int32(o);
	return __allocate_Int32(__div_int32(p->value, v));
}
static VALUE
r_Int16_mod(VALUE self, VALUE o)
{
	Int16 *p;
	Data_Get_Struct(self, Int16, p);
	int32_t v = __accept_int32(o);
	return __allocate_Int32(__mod_int32(p->value, v));
}
static VALUE
r_Int16_add(VALUE self, VALUE o)
{
	Int16 *p;
	Data_Get_Struct(self, Int16, p);
	int32_t v = __accept_int32(o);
	return __allocate_Int32(p->value + v);
}
static VALUE
r_Int16_sub(VALUE self, VALUE o)
{
	Int16 *p;
	Data_Get_Struct(self, Int16, p);
	int32_t v = __accept_int32(o);
	return __allocate_Int32(p->value - v);
}
static VALUE
r_Int16_cmp(VALUE self, VALUE o)
{
	Int16 *p;
	Data_Get_Struct(self, Int16, p);
	int32_t v = __accept_int32(o);
	if (p->value < v)
		return r_INT32_M1;
	if (p->value > v)
		return r_INT32_1;
	return r_INT32_0;
}
static VALUE
r_Int16_lt(VALUE self, VALUE o)
{
	Int16 *p;
	Data_Get_Struct(self, Int16, p);
	int32_t v = __accept_int32(o);
	return p->value < v ? Qtrue : Qfalse;
}
static VALUE
r_Int16_le(VALUE self, VALUE o)
{
	Int16 *p;
	Data_Get_Struct(self, Int16, p);
	int32_t v = __accept_int32(o);
	return p->value <= v ? Qtrue : Qfalse;
}
static VALUE
r_Int16_ge(VALUE self, VALUE o)
{
	Int16 *p;
	Data_Get_Struct(self, Int16, p);
	int32_t v = __accept_int32(o);
	return p->value >= v ? Qtrue : Qfalse;
}
static VALUE
r_Int16_gt(VALUE self, VALUE o)
{
	Int16 *p;
	Data_Get_Struct(self, Int16, p);
	int32_t v = __accept_int32(o);
	return p->value > v ? Qtrue : Qfalse;
}
static VALUE
r_Int16_inv(VALUE self)
{
	Int16 *p;
	Data_Get_Struct(self, Int16, p);
	return __allocate_Int32(~p->value);
}
static VALUE
r_Int16_shl(VALUE self, VALUE o)
{
	Int16 *p;
	Data_Get_Struct(self, Int16, p);
	int32_t d = __accept_int32(o);
	return __allocate_Int32(p->value << d);
}
static VALUE
r_Int16_shr(VALUE self, VALUE o)
{
	Int16 *p;
	Data_Get_Struct(self, Int16, p);
	int32_t d = __accept_int32(o);
	return __allocate_Int32(p->value >> d);
}
static VALUE
r_Int16_ushr(VALUE self, VALUE o)
{
	Int16 *p;
	Data_Get_Struct(self, Int16, p);
	uint32_t u = (uint32_t) p->value;
	int32_t d = __accept_int32(o);
	return __allocate_Int32((int32_t) (u >> d));
}
static VALUE
r_Int16_and(VALUE self, VALUE o)
{
	Int16 *p;
	Data_Get_Struct(self, Int16, p);
	int32_t v = __accept_int32(o);
	return __allocate_Int32(p->value & v);
}
static VALUE
r_Int16_xor(VALUE self, VALUE o)
{
	Int16 *p;
	Data_Get_Struct(self, Int16, p);
	int32_t v = __accept_int32(o);
	return __allocate_Int32(p->value ^ v);
}
static VALUE
r_Int16_or(VALUE self, VALUE o)
{
	Int16 *p;
	Data_Get_Struct(self, Int16, p);
	int32_t v = __accept_int32(o);
	return __allocate_Int32(p->value | v);
}
static VALUE
r_Int16_is$zero(VALUE self)
{
	Int16 *p;
	Data_Get_Struct(self, Int16, p);
	return p->value == 0 ? Qtrue : Qfalse;
}
static VALUE
r_Int32_new(VALUE self, VALUE o)
{
	int32_t value = __accept_int32(o);
	return __allocate_Int32(value);
}
static VALUE
r_Int32_equals(VALUE self, VALUE o)
{
	Int32 *p;
	Data_Get_Struct(self, Int32, p);
	if (TYPE(o) == T_DATA) {
		if (RBASIC(o)->klass == r_Int32) {
			Int32 *op;
			Data_Get_Struct(o, Int32, op);
			if (p->value == op->value)
				return Qtrue;
		}
	}
	return Qfalse;
}
static VALUE
r_Int32_hashCode(VALUE self)
{
	Int32 *p;
	Data_Get_Struct(self, Int32, p);
	return __allocate_Int32(__hash_int32(p->value));
}
static VALUE
r_Int32_toString(VALUE self)
{
	Int32 *p;
	Data_Get_Struct(self, Int32, p);
	return rb_funcall(__int32_to_s(p->value), TO_J_ID, 0);
}
static VALUE
r_Int32_hash(VALUE self)
{
	Int32 *p;
	Data_Get_Struct(self, Int32, p);
	return LONG2NUM((long) __hash_int32(p->value));
}
static VALUE
r_Int32_to_s(VALUE self)
{
	Int32 *p;
	Data_Get_Struct(self, Int32, p);
	return __int32_to_s(p->value);
}
static VALUE
r_Int32_to_byte(VALUE self)
{
	Int32 *p;
	Data_Get_Struct(self, Int32, p);
	return __allocate_Byte((int8_t) p->value);
}
static VALUE
r_Int32_to_char(VALUE self)
{
	Int32 *p;
	Data_Get_Struct(self, Int32, p);
	return __allocate_Char((uint16_t) p->value);
}
static VALUE
r_Int32_to_int16(VALUE self)
{
	Int32 *p;
	Data_Get_Struct(self, Int32, p);
	return __allocate_Int16((int16_t) p->value);
}
static VALUE
r_Int32_to_int32(VALUE self)
{
	return self;
}
static VALUE
r_Int32_to_int64(VALUE self)
{
	Int32 *p;
	Data_Get_Struct(self, Int32, p);
	return __allocate_Int64((int64_t) p->value);
}
static VALUE
r_Int32_to_float32(VALUE self)
{
	Int32 *p;
	Data_Get_Struct(self, Int32, p);
	return __allocate_Float32((float) p->value);
}
static VALUE
r_Int32_to_float64(VALUE self)
{
	Int32 *p;
	Data_Get_Struct(self, Int32, p);
	return __allocate_Float64((double) p->value);
}
static VALUE
r_Int32_to_i(VALUE self)
{
	Int32 *p;
	Data_Get_Struct(self, Int32, p);
	return __int32_to_i(p->value);
}
static VALUE
r_Int32_to_f(VALUE self)
{
	Int32 *p;
	Data_Get_Struct(self, Int32, p);
	return __int32_to_f(p->value);
}
static VALUE
r_Int32_eq(VALUE self, VALUE o)
{
	Int32 *p;
	Data_Get_Struct(self, Int32, p);
	int32_t v = __accept_int32(o);
	return p->value == v ? Qtrue : Qfalse;
}
static VALUE
r_Int32_ne(VALUE self, VALUE o)
{
	Int32 *p;
	Data_Get_Struct(self, Int32, p);
	int32_t v = __accept_int32(o);
	return p->value != v ? Qtrue : Qfalse;
}
static VALUE
r_Int32_pos(VALUE self)
{
	return self;
}
static VALUE
r_Int32_neg(VALUE self)
{
	Int32 *p;
	Data_Get_Struct(self, Int32, p);
	return __allocate_Int32(-p->value);
}
static VALUE
r_Int32_mul(VALUE self, VALUE o)
{
	Int32 *p;
	Data_Get_Struct(self, Int32, p);
	int32_t v = __accept_int32(o);
	return __allocate_Int32(p->value * v);
}
static VALUE
r_Int32_div(VALUE self, VALUE o)
{
	Int32 *p;
	Data_Get_Struct(self, Int32, p);
	int32_t v = __accept_int32(o);
	return __allocate_Int32(__div_int32(p->value, v));
}
static VALUE
r_Int32_mod(VALUE self, VALUE o)
{
	Int32 *p;
	Data_Get_Struct(self, Int32, p);
	int32_t v = __accept_int32(o);
	return __allocate_Int32(__mod_int32(p->value, v));
}
static VALUE
r_Int32_add(VALUE self, VALUE o)
{
	Int32 *p;
	Data_Get_Struct(self, Int32, p);
	int32_t v = __accept_int32(o);
	return __allocate_Int32(p->value + v);
}
static VALUE
r_Int32_sub(VALUE self, VALUE o)
{
	Int32 *p;
	Data_Get_Struct(self, Int32, p);
	int32_t v = __accept_int32(o);
	return __allocate_Int32(p->value - v);
}
static VALUE
r_Int32_cmp(VALUE self, VALUE o)
{
	Int32 *p;
	Data_Get_Struct(self, Int32, p);
	int32_t v = __accept_int32(o);
	if (p->value < v)
		return r_INT32_M1;
	if (p->value > v)
		return r_INT32_1;
	return r_INT32_0;
}
static VALUE
r_Int32_lt(VALUE self, VALUE o)
{
	Int32 *p;
	Data_Get_Struct(self, Int32, p);
	int32_t v = __accept_int32(o);
	return p->value < v ? Qtrue : Qfalse;
}
static VALUE
r_Int32_le(VALUE self, VALUE o)
{
	Int32 *p;
	Data_Get_Struct(self, Int32, p);
	int32_t v = __accept_int32(o);
	return p->value <= v ? Qtrue : Qfalse;
}
static VALUE
r_Int32_ge(VALUE self, VALUE o)
{
	Int32 *p;
	Data_Get_Struct(self, Int32, p);
	int32_t v = __accept_int32(o);
	return p->value >= v ? Qtrue : Qfalse;
}
static VALUE
r_Int32_gt(VALUE self, VALUE o)
{
	Int32 *p;
	Data_Get_Struct(self, Int32, p);
	int32_t v = __accept_int32(o);
	return p->value > v ? Qtrue : Qfalse;
}
static VALUE
r_Int32_inv(VALUE self)
{
	Int32 *p;
	Data_Get_Struct(self, Int32, p);
	return __allocate_Int32(~p->value);
}
static VALUE
r_Int32_shl(VALUE self, VALUE o)
{
	Int32 *p;
	Data_Get_Struct(self, Int32, p);
	int32_t d = __accept_int32(o);
	return __allocate_Int32(p->value << d);
}
static VALUE
r_Int32_shr(VALUE self, VALUE o)
{
	Int32 *p;
	Data_Get_Struct(self, Int32, p);
	int32_t d = __accept_int32(o);
	return __allocate_Int32(p->value >> d);
}
static VALUE
r_Int32_ushr(VALUE self, VALUE o)
{
	Int32 *p;
	Data_Get_Struct(self, Int32, p);
	uint32_t u = (uint32_t) p->value;
	int32_t d = __accept_int32(o);
	return __allocate_Int32((int32_t) (u >> d));
}
static VALUE
r_Int32_and(VALUE self, VALUE o)
{
	Int32 *p;
	Data_Get_Struct(self, Int32, p);
	int32_t v = __accept_int32(o);
	return __allocate_Int32(p->value & v);
}
static VALUE
r_Int32_xor(VALUE self, VALUE o)
{
	Int32 *p;
	Data_Get_Struct(self, Int32, p);
	int32_t v = __accept_int32(o);
	return __allocate_Int32(p->value ^ v);
}
static VALUE
r_Int32_or(VALUE self, VALUE o)
{
	Int32 *p;
	Data_Get_Struct(self, Int32, p);
	int32_t v = __accept_int32(o);
	return __allocate_Int32(p->value | v);
}
static VALUE
r_Int32_is$zero(VALUE self)
{
	Int32 *p;
	Data_Get_Struct(self, Int32, p);
	return p->value == 0 ? Qtrue : Qfalse;
}
static VALUE
r_Int32_bang$to_int32(VALUE self)
{
	return self;
}
static VALUE
r_Int32_bang$to_int64(VALUE self)
{
	Int32 *p;
	Data_Get_Struct(self, Int32, p);
	return __allocate_Int64((int64_t) p->value);
}
static VALUE
r_Int32_to_fixnum(VALUE self)
{
	Int32 *p;
	Data_Get_Struct(self, Int32, p);
	return LONG2NUM((long) p->value);
}
static VALUE
r_Int32_to_hex(VALUE self)
{
	Int32 *p;
	Data_Get_Struct(self, Int32, p);
	return __int32_to_hex(p->value);
}
static VALUE
r_Int32_chr(VALUE self)
{
	Int32 *p;
	Data_Get_Struct(self, Int32, p);
	return __int32_chr(p->value);
}
static VALUE
r_Int32_succ(VALUE self)
{
	Int32 *p;
	Data_Get_Struct(self, Int32, p);
	if (p->value == 0x7FFFFFFF)
		rb_raise(rb_eStopIteration, "StopIteration");
	return __allocate_Int32(p->value + 1);
}
static VALUE
r_Int32_rol(VALUE self, VALUE o)
{
	Int32 *p;
	Data_Get_Struct(self, Int32, p);
	uint32_t u = (uint32_t) p->value;
	int32_t d = __accept_int32(o);
	return __allocate_Int32((int32_t) (u << d | u >> (32 - d)));
}
static VALUE
r_Int32_ror(VALUE self, VALUE o)
{
	Int32 *p;
	Data_Get_Struct(self, Int32, p);
	uint32_t u = (uint32_t) p->value;
	int32_t d = __accept_int32(o);
	return __allocate_Int32((int32_t) (u >> d | u << (32 - d)));
}
static VALUE
r_Int32_count(VALUE self)
{
	Int32 *p;
	Data_Get_Struct(self, Int32, p);
	int32_t c = 0;
	uint32_t u = (uint32_t) p->value;
	while (u != 0) {
		u &= u - 1;
		c += 1;
	}
	return __allocate_Int32(c);
}
static VALUE
r_Int32_signum(VALUE self)
{
	Int32 *p;
	Data_Get_Struct(self, Int32, p);
	if (p->value < 0)
		return r_INT32_M1;
	if (p->value > 0)
		return r_INT32_1;
	return r_INT32_0;
}
static VALUE
r_Int32_times(VALUE self)
{
	Int32 *p;
	Data_Get_Struct(self, Int32, p);
	int32_t i = 0;
	while (i < p->value) {
		rb_yield(__allocate_Int32(i));
		i += 1;
	}
	return Qnil;
}
static VALUE
r_Int32_upto(VALUE self, VALUE o)
{
	Int32 *p;
	Data_Get_Struct(self, Int32, p);
	int32_t i = p->value;
	int32_t v = __accept_int32(o);
	if (i <= v) {
		while (1) {
			rb_yield(__allocate_Int32(i));
			if (i == v)
				break;
			i += 1;
		}
	}
	return Qnil;
}
static VALUE
r_Int32_downto(VALUE self, VALUE o)
{
	Int32 *p;
	Data_Get_Struct(self, Int32, p);
	int32_t i = p->value;
	int32_t v = __accept_int32(o);
	if (i >= v) {
		while (1) {
			rb_yield(__allocate_Int32(i));
			if (i == v)
				break;
			i -= 1;
		}
	}
	return Qnil;
}
static VALUE
r_Int64_new(VALUE self, VALUE o)
{
	int64_t value = __accept_int64(o);
	return __allocate_Int64(value);
}
static VALUE
r_Int64_equals(VALUE self, VALUE o)
{
	Int64 *p;
	Data_Get_Struct(self, Int64, p);
	if (TYPE(o) == T_DATA) {
		if (RBASIC(o)->klass == r_Int64) {
			Int64 *op;
			Data_Get_Struct(o, Int64, op);
			if (p->value == op->value)
				return Qtrue;
		}
	}
	return Qfalse;
}
static VALUE
r_Int64_hashCode(VALUE self)
{
	Int64 *p;
	Data_Get_Struct(self, Int64, p);
	return __allocate_Int32(__hash_int64(p->value));
}
static VALUE
r_Int64_toString(VALUE self)
{
	Int64 *p;
	Data_Get_Struct(self, Int64, p);
	return rb_funcall(__int64_to_s(p->value), TO_J_ID, 0);
}
static VALUE
r_Int64_hash(VALUE self)
{
	Int64 *p;
	Data_Get_Struct(self, Int64, p);
	return LONG2NUM((long) __hash_int64(p->value));
}
static VALUE
r_Int64_to_s(VALUE self)
{
	Int64 *p;
	Data_Get_Struct(self, Int64, p);
	return __int64_to_s(p->value);
}
static VALUE
r_Int64_to_byte(VALUE self)
{
	Int64 *p;
	Data_Get_Struct(self, Int64, p);
	return __allocate_Byte((int8_t) p->value);
}
static VALUE
r_Int64_to_char(VALUE self)
{
	Int64 *p;
	Data_Get_Struct(self, Int64, p);
	return __allocate_Char((uint16_t) p->value);
}
static VALUE
r_Int64_to_int16(VALUE self)
{
	Int64 *p;
	Data_Get_Struct(self, Int64, p);
	return __allocate_Int16((int16_t) p->value);
}
static VALUE
r_Int64_to_int32(VALUE self)
{
	Int64 *p;
	Data_Get_Struct(self, Int64, p);
	return __allocate_Int32((int32_t) p->value);
}
static VALUE
r_Int64_to_int64(VALUE self)
{
	return self;
}
static VALUE
r_Int64_to_float32(VALUE self)
{
	Int64 *p;
	Data_Get_Struct(self, Int64, p);
	return __allocate_Float32((float) p->value);
}
static VALUE
r_Int64_to_float64(VALUE self)
{
	Int64 *p;
	Data_Get_Struct(self, Int64, p);
	return __allocate_Float64((double) p->value);
}
static VALUE
r_Int64_to_i(VALUE self)
{
	Int64 *p;
	Data_Get_Struct(self, Int64, p);
	return __int64_to_i(p->value);
}
static VALUE
r_Int64_to_f(VALUE self)
{
	Int64 *p;
	Data_Get_Struct(self, Int64, p);
	return __int64_to_f(p->value);
}
static VALUE
r_Int64_eq(VALUE self, VALUE o)
{
	Int64 *p;
	Data_Get_Struct(self, Int64, p);
	int64_t v = __accept_int64(o);
	return p->value == v ? Qtrue : Qfalse;
}
static VALUE
r_Int64_ne(VALUE self, VALUE o)
{
	Int64 *p;
	Data_Get_Struct(self, Int64, p);
	int64_t v = __accept_int64(o);
	return p->value != v ? Qtrue : Qfalse;
}
static VALUE
r_Int64_pos(VALUE self)
{
	return self;
}
static VALUE
r_Int64_neg(VALUE self)
{
	Int64 *p;
	Data_Get_Struct(self, Int64, p);
	return __allocate_Int64(-p->value);
}
static VALUE
r_Int64_mul(VALUE self, VALUE o)
{
	Int64 *p;
	Data_Get_Struct(self, Int64, p);
	int64_t v = __accept_int64(o);
	return __allocate_Int64(p->value * v);
}
static VALUE
r_Int64_div(VALUE self, VALUE o)
{
	Int64 *p;
	Data_Get_Struct(self, Int64, p);
	int64_t v = __accept_int64(o);
	return __allocate_Int64(__div_int64(p->value, v));
}
static VALUE
r_Int64_mod(VALUE self, VALUE o)
{
	Int64 *p;
	Data_Get_Struct(self, Int64, p);
	int64_t v = __accept_int64(o);
	return __allocate_Int64(__mod_int64(p->value, v));
}
static VALUE
r_Int64_add(VALUE self, VALUE o)
{
	Int64 *p;
	Data_Get_Struct(self, Int64, p);
	int64_t v = __accept_int64(o);
	return __allocate_Int64(p->value + v);
}
static VALUE
r_Int64_sub(VALUE self, VALUE o)
{
	Int64 *p;
	Data_Get_Struct(self, Int64, p);
	int64_t v = __accept_int64(o);
	return __allocate_Int64(p->value - v);
}
static VALUE
r_Int64_cmp(VALUE self, VALUE o)
{
	Int64 *p;
	Data_Get_Struct(self, Int64, p);
	int64_t v = __accept_int64(o);
	if (p->value < v)
		return r_INT32_M1;
	if (p->value > v)
		return r_INT32_1;
	return r_INT32_0;
}
static VALUE
r_Int64_lt(VALUE self, VALUE o)
{
	Int64 *p;
	Data_Get_Struct(self, Int64, p);
	int64_t v = __accept_int64(o);
	return p->value < v ? Qtrue : Qfalse;
}
static VALUE
r_Int64_le(VALUE self, VALUE o)
{
	Int64 *p;
	Data_Get_Struct(self, Int64, p);
	int64_t v = __accept_int64(o);
	return p->value <= v ? Qtrue : Qfalse;
}
static VALUE
r_Int64_ge(VALUE self, VALUE o)
{
	Int64 *p;
	Data_Get_Struct(self, Int64, p);
	int64_t v = __accept_int64(o);
	return p->value >= v ? Qtrue : Qfalse;
}
static VALUE
r_Int64_gt(VALUE self, VALUE o)
{
	Int64 *p;
	Data_Get_Struct(self, Int64, p);
	int64_t v = __accept_int64(o);
	return p->value > v ? Qtrue : Qfalse;
}
static VALUE
r_Int64_inv(VALUE self)
{
	Int64 *p;
	Data_Get_Struct(self, Int64, p);
	return __allocate_Int64(~p->value);
}
static VALUE
r_Int64_shl(VALUE self, VALUE o)
{
	Int64 *p;
	Data_Get_Struct(self, Int64, p);
	int32_t d = __accept_int32(o);
	return __allocate_Int64(p->value << d);
}
static VALUE
r_Int64_shr(VALUE self, VALUE o)
{
	Int64 *p;
	Data_Get_Struct(self, Int64, p);
	int32_t d = __accept_int32(o);
	return __allocate_Int64(p->value >> d);
}
static VALUE
r_Int64_ushr(VALUE self, VALUE o)
{
	Int64 *p;
	Data_Get_Struct(self, Int64, p);
	uint64_t u = (uint64_t) p->value;
	int32_t d = __accept_int32(o);
	return __allocate_Int64((int64_t) (u >> d));
}
static VALUE
r_Int64_and(VALUE self, VALUE o)
{
	Int64 *p;
	Data_Get_Struct(self, Int64, p);
	int64_t v = __accept_int64(o);
	return __allocate_Int64(p->value & v);
}
static VALUE
r_Int64_xor(VALUE self, VALUE o)
{
	Int64 *p;
	Data_Get_Struct(self, Int64, p);
	int64_t v = __accept_int64(o);
	return __allocate_Int64(p->value ^ v);
}
static VALUE
r_Int64_or(VALUE self, VALUE o)
{
	Int64 *p;
	Data_Get_Struct(self, Int64, p);
	int64_t v = __accept_int64(o);
	return __allocate_Int64(p->value | v);
}
static VALUE
r_Int64_is$zero(VALUE self)
{
	Int64 *p;
	Data_Get_Struct(self, Int64, p);
	return p->value == 0 ? Qtrue : Qfalse;
}
static VALUE
r_Int64_bang$to_int32(VALUE self)
{
	Int64 *p;
	Data_Get_Struct(self, Int64, p);
	if (p->value < -0x0000000080000000L || p->value > 0x000000007FFFFFFFL)
		rb_raise(rb_eRangeError, "int64 too big to convert to int32");
	return __allocate_Int32((int32_t) p->value);
}
static VALUE
r_Int64_bang$to_int64(VALUE self)
{
	return self;
}
static VALUE
r_Int64_to_fixnum(VALUE self)
{
	Int64 *p;
	Data_Get_Struct(self, Int64, p);
	return LONG2NUM((long) p->value);
}
static VALUE
r_Int64_to_hex(VALUE self)
{
	Int64 *p;
	Data_Get_Struct(self, Int64, p);
	return __int64_to_hex(p->value);
}
static VALUE
r_Int64_succ(VALUE self)
{
	Int64 *p;
	Data_Get_Struct(self, Int64, p);
	if (p->value == 0x7FFFFFFFFFFFFFFFL)
		rb_raise(rb_eStopIteration, "StopIteration");
	return __allocate_Int64(p->value + 1);
}
static VALUE
r_Int64_rol(VALUE self, VALUE o)
{
	Int64 *p;
	Data_Get_Struct(self, Int64, p);
	uint64_t u = (uint64_t) p->value;
	int32_t d = __accept_int32(o);
	return __allocate_Int64((int64_t) (u << d | u >> (64 - d)));
}
static VALUE
r_Int64_ror(VALUE self, VALUE o)
{
	Int64 *p;
	Data_Get_Struct(self, Int64, p);
	uint64_t u = (uint64_t) p->value;
	int32_t d = __accept_int32(o);
	return __allocate_Int64((int64_t) (u >> d | u << (64 - d)));
}
static VALUE
r_Int64_count(VALUE self)
{
	Int64 *p;
	Data_Get_Struct(self, Int64, p);
	int32_t c = 0;
	uint64_t u = (uint64_t) p->value;
	while (u != 0) {
		u &= u - 1;
		c += 1;
	}
	return __allocate_Int32(c);
}
static VALUE
r_Int64_signum(VALUE self)
{
	Int64 *p;
	Data_Get_Struct(self, Int64, p);
	if (p->value < 0)
		return r_INT32_M1;
	if (p->value > 0)
		return r_INT32_1;
	return r_INT32_0;
}
static VALUE
r_Int64_times(VALUE self)
{
	Int64 *p;
	Data_Get_Struct(self, Int64, p);
	int64_t i = 0;
	while (i < p->value) {
		rb_yield(__allocate_Int64(i));
		i += 1;
	}
	return Qnil;
}
static VALUE
r_Float32_new(VALUE self, VALUE o)
{
	float value = __accept_float32(o);
	return __allocate_Float32(value);
}
static VALUE
r_Float32_equals(VALUE self, VALUE o)
{
	Float32 *p;
	Data_Get_Struct(self, Float32, p);
	if (TYPE(o) == T_DATA) {
		if (RBASIC(o)->klass == r_Float32) {
			Float32 *op;
			Data_Get_Struct(o, Float32, op);
			if (p->value == op->value)
				return Qtrue;
		}
	}
	return Qfalse;
}
static VALUE
r_Float32_hashCode(VALUE self)
{
	Float32 *p;
	Data_Get_Struct(self, Float32, p);
	return __allocate_Int32(__hash_float32(p->value));
}
static VALUE
r_Float32_toString(VALUE self)
{
	Float32 *p;
	Data_Get_Struct(self, Float32, p);
	return rb_funcall(__float32_to_s(p->value), TO_J_ID, 0);
}
static VALUE
r_Float32_hash(VALUE self)
{
	Float32 *p;
	Data_Get_Struct(self, Float32, p);
	return LONG2NUM((long) __hash_float32(p->value));
}
static VALUE
r_Float32_to_s(VALUE self)
{
	Float32 *p;
	Data_Get_Struct(self, Float32, p);
	return __float32_to_s(p->value);
}
static VALUE
r_Float32_to_byte(VALUE self)
{
	Float32 *p;
	Data_Get_Struct(self, Float32, p);
	return __allocate_Byte((int8_t) p->value);
}
static VALUE
r_Float32_to_char(VALUE self)
{
	Float32 *p;
	Data_Get_Struct(self, Float32, p);
	return __allocate_Char((uint16_t) p->value);
}
static VALUE
r_Float32_to_int16(VALUE self)
{
	Float32 *p;
	Data_Get_Struct(self, Float32, p);
	return __allocate_Int16((int16_t) p->value);
}
static VALUE
r_Float32_to_int32(VALUE self)
{
	Float32 *p;
	Data_Get_Struct(self, Float32, p);
	return __allocate_Int32((int32_t) p->value);
}
static VALUE
r_Float32_to_int64(VALUE self)
{
	Float32 *p;
	Data_Get_Struct(self, Float32, p);
	return __allocate_Int64((int64_t) p->value);
}
static VALUE
r_Float32_to_float32(VALUE self)
{
	return self;
}
static VALUE
r_Float32_to_float64(VALUE self)
{
	Float32 *p;
	Data_Get_Struct(self, Float32, p);
	return __allocate_Float64((double) p->value);
}
static VALUE
r_Float32_to_i(VALUE self)
{
	Float32 *p;
	Data_Get_Struct(self, Float32, p);
	return __float32_to_i(p->value);
}
static VALUE
r_Float32_to_f(VALUE self)
{
	Float32 *p;
	Data_Get_Struct(self, Float32, p);
	return __float32_to_f(p->value);
}
static VALUE
r_Float32_eq(VALUE self, VALUE o)
{
	Float32 *p;
	Data_Get_Struct(self, Float32, p);
	float v = __accept_float32(o);
	return p->value == v ? Qtrue : Qfalse;
}
static VALUE
r_Float32_ne(VALUE self, VALUE o)
{
	Float32 *p;
	Data_Get_Struct(self, Float32, p);
	float v = __accept_float32(o);
	return p->value != v ? Qtrue : Qfalse;
}
static VALUE
r_Float32_pos(VALUE self)
{
	return self;
}
static VALUE
r_Float32_neg(VALUE self)
{
	Float32 *p;
	Data_Get_Struct(self, Float32, p);
	return __allocate_Float32(-p->value);
}
static VALUE
r_Float32_mul(VALUE self, VALUE o)
{
	Float32 *p;
	Data_Get_Struct(self, Float32, p);
	float v = __accept_float32(o);
	return __allocate_Float32(p->value * v);
}
static VALUE
r_Float32_div(VALUE self, VALUE o)
{
	Float32 *p;
	Data_Get_Struct(self, Float32, p);
	float v = __accept_float32(o);
	return __allocate_Float32(__div_float32(p->value, v));
}
static VALUE
r_Float32_mod(VALUE self, VALUE o)
{
	Float32 *p;
	Data_Get_Struct(self, Float32, p);
	float v = __accept_float32(o);
	return __allocate_Float32(__mod_float32(p->value, v));
}
static VALUE
r_Float32_add(VALUE self, VALUE o)
{
	Float32 *p;
	Data_Get_Struct(self, Float32, p);
	float v = __accept_float32(o);
	return __allocate_Float32(p->value + v);
}
static VALUE
r_Float32_sub(VALUE self, VALUE o)
{
	Float32 *p;
	Data_Get_Struct(self, Float32, p);
	float v = __accept_float32(o);
	return __allocate_Float32(p->value - v);
}
static VALUE
r_Float32_cmp(VALUE self, VALUE o)
{
	Float32 *p;
	Data_Get_Struct(self, Float32, p);
	float v = __accept_float32(o);
	if (p->value < v)
		return r_INT32_M1;
	if (p->value > v)
		return r_INT32_1;
	return r_INT32_0;
}
static VALUE
r_Float32_lt(VALUE self, VALUE o)
{
	Float32 *p;
	Data_Get_Struct(self, Float32, p);
	float v = __accept_float32(o);
	return p->value < v ? Qtrue : Qfalse;
}
static VALUE
r_Float32_le(VALUE self, VALUE o)
{
	Float32 *p;
	Data_Get_Struct(self, Float32, p);
	float v = __accept_float32(o);
	return p->value <= v ? Qtrue : Qfalse;
}
static VALUE
r_Float32_ge(VALUE self, VALUE o)
{
	Float32 *p;
	Data_Get_Struct(self, Float32, p);
	float v = __accept_float32(o);
	return p->value >= v ? Qtrue : Qfalse;
}
static VALUE
r_Float32_gt(VALUE self, VALUE o)
{
	Float32 *p;
	Data_Get_Struct(self, Float32, p);
	float v = __accept_float32(o);
	return p->value > v ? Qtrue : Qfalse;
}
static VALUE
r_Float32_is$zero(VALUE self)
{
	Float32 *p;
	Data_Get_Struct(self, Float32, p);
	return p->value == 0 ? Qtrue : Qfalse;
}
static VALUE
r_Float32_is$nan(VALUE self)
{
	Float32 *p;
	Data_Get_Struct(self, Float32, p);
	return isnan(p->value) ? Qtrue : Qfalse;
}
static VALUE
r_Float32_is$infinite(VALUE self)
{
	Float32 *p;
	Data_Get_Struct(self, Float32, p);
	return isinf(p->value) ? Qtrue : Qfalse;
}
static VALUE
r_Float32_is$finite(VALUE self)
{
	Float32 *p;
	Data_Get_Struct(self, Float32, p);
	return !isnan(p->value) && !isinf(p->value) ? Qtrue : Qfalse;
}
static VALUE
r_Float64_new(VALUE self, VALUE o)
{
	double value = __accept_float64(o);
	return __allocate_Float64(value);
}
static VALUE
r_Float64_equals(VALUE self, VALUE o)
{
	Float64 *p;
	Data_Get_Struct(self, Float64, p);
	if (TYPE(o) == T_DATA) {
		if (RBASIC(o)->klass == r_Float64) {
			Float64 *op;
			Data_Get_Struct(o, Float64, op);
			if (p->value == op->value)
				return Qtrue;
		}
	}
	return Qfalse;
}
static VALUE
r_Float64_hashCode(VALUE self)
{
	Float64 *p;
	Data_Get_Struct(self, Float64, p);
	return __allocate_Int32(__hash_float64(p->value));
}
static VALUE
r_Float64_toString(VALUE self)
{
	Float64 *p;
	Data_Get_Struct(self, Float64, p);
	return rb_funcall(__float64_to_s(p->value), TO_J_ID, 0);
}
static VALUE
r_Float64_hash(VALUE self)
{
	Float64 *p;
	Data_Get_Struct(self, Float64, p);
	return LONG2NUM((long) __hash_float64(p->value));
}
static VALUE
r_Float64_to_s(VALUE self)
{
	Float64 *p;
	Data_Get_Struct(self, Float64, p);
	return __float64_to_s(p->value);
}
static VALUE
r_Float64_to_byte(VALUE self)
{
	Float64 *p;
	Data_Get_Struct(self, Float64, p);
	return __allocate_Byte((int8_t) p->value);
}
static VALUE
r_Float64_to_char(VALUE self)
{
	Float64 *p;
	Data_Get_Struct(self, Float64, p);
	return __allocate_Char((uint16_t) p->value);
}
static VALUE
r_Float64_to_int16(VALUE self)
{
	Float64 *p;
	Data_Get_Struct(self, Float64, p);
	return __allocate_Int16((int16_t) p->value);
}
static VALUE
r_Float64_to_int32(VALUE self)
{
	Float64 *p;
	Data_Get_Struct(self, Float64, p);
	return __allocate_Int32((int32_t) p->value);
}
static VALUE
r_Float64_to_int64(VALUE self)
{
	Float64 *p;
	Data_Get_Struct(self, Float64, p);
	return __allocate_Int64((int64_t) p->value);
}
static VALUE
r_Float64_to_float32(VALUE self)
{
	Float64 *p;
	Data_Get_Struct(self, Float64, p);
	return __allocate_Float32((float) p->value);
}
static VALUE
r_Float64_to_float64(VALUE self)
{
	return self;
}
static VALUE
r_Float64_to_i(VALUE self)
{
	Float64 *p;
	Data_Get_Struct(self, Float64, p);
	return __float64_to_i(p->value);
}
static VALUE
r_Float64_to_f(VALUE self)
{
	Float64 *p;
	Data_Get_Struct(self, Float64, p);
	return __float64_to_f(p->value);
}
static VALUE
r_Float64_eq(VALUE self, VALUE o)
{
	Float64 *p;
	Data_Get_Struct(self, Float64, p);
	double v = __accept_float64(o);
	return p->value == v ? Qtrue : Qfalse;
}
static VALUE
r_Float64_ne(VALUE self, VALUE o)
{
	Float64 *p;
	Data_Get_Struct(self, Float64, p);
	double v = __accept_float64(o);
	return p->value != v ? Qtrue : Qfalse;
}
static VALUE
r_Float64_pos(VALUE self)
{
	return self;
}
static VALUE
r_Float64_neg(VALUE self)
{
	Float64 *p;
	Data_Get_Struct(self, Float64, p);
	return __allocate_Float64(-p->value);
}
static VALUE
r_Float64_mul(VALUE self, VALUE o)
{
	Float64 *p;
	Data_Get_Struct(self, Float64, p);
	double v = __accept_float64(o);
	return __allocate_Float64(p->value * v);
}
static VALUE
r_Float64_div(VALUE self, VALUE o)
{
	Float64 *p;
	Data_Get_Struct(self, Float64, p);
	double v = __accept_float64(o);
	return __allocate_Float64(__div_float64(p->value, v));
}
static VALUE
r_Float64_mod(VALUE self, VALUE o)
{
	Float64 *p;
	Data_Get_Struct(self, Float64, p);
	double v = __accept_float64(o);
	return __allocate_Float64(__mod_float64(p->value, v));
}
static VALUE
r_Float64_add(VALUE self, VALUE o)
{
	Float64 *p;
	Data_Get_Struct(self, Float64, p);
	double v = __accept_float64(o);
	return __allocate_Float64(p->value + v);
}
static VALUE
r_Float64_sub(VALUE self, VALUE o)
{
	Float64 *p;
	Data_Get_Struct(self, Float64, p);
	double v = __accept_float64(o);
	return __allocate_Float64(p->value - v);
}
static VALUE
r_Float64_cmp(VALUE self, VALUE o)
{
	Float64 *p;
	Data_Get_Struct(self, Float64, p);
	double v = __accept_float64(o);
	if (p->value < v)
		return r_INT32_M1;
	if (p->value > v)
		return r_INT32_1;
	return r_INT32_0;
}
static VALUE
r_Float64_lt(VALUE self, VALUE o)
{
	Float64 *p;
	Data_Get_Struct(self, Float64, p);
	double v = __accept_float64(o);
	return p->value < v ? Qtrue : Qfalse;
}
static VALUE
r_Float64_le(VALUE self, VALUE o)
{
	Float64 *p;
	Data_Get_Struct(self, Float64, p);
	double v = __accept_float64(o);
	return p->value <= v ? Qtrue : Qfalse;
}
static VALUE
r_Float64_ge(VALUE self, VALUE o)
{
	Float64 *p;
	Data_Get_Struct(self, Float64, p);
	double v = __accept_float64(o);
	return p->value >= v ? Qtrue : Qfalse;
}
static VALUE
r_Float64_gt(VALUE self, VALUE o)
{
	Float64 *p;
	Data_Get_Struct(self, Float64, p);
	double v = __accept_float64(o);
	return p->value > v ? Qtrue : Qfalse;
}
static VALUE
r_Float64_is$zero(VALUE self)
{
	Float64 *p;
	Data_Get_Struct(self, Float64, p);
	return p->value == 0 ? Qtrue : Qfalse;
}
static VALUE
r_Float64_is$nan(VALUE self)
{
	Float64 *p;
	Data_Get_Struct(self, Float64, p);
	return isnan(p->value) ? Qtrue : Qfalse;
}
static VALUE
r_Float64_is$infinite(VALUE self)
{
	Float64 *p;
	Data_Get_Struct(self, Float64, p);
	return isinf(p->value) ? Qtrue : Qfalse;
}
static VALUE
r_Float64_is$finite(VALUE self)
{
	Float64 *p;
	Data_Get_Struct(self, Float64, p);
	return !isnan(p->value) && !isinf(p->value) ? Qtrue : Qfalse;
}
struct JavaException {
	VALUE cause;
};
typedef struct JavaException JavaException;
static void
__mark_JavaException(void *__p)
{
	JavaException *p = __p;
	rb_gc_mark(p->cause);
	return;
}
static void
__free_JavaException(void *__p)
{
	JavaException *p = __p;
	xfree(p);
	return;
}
static VALUE r_JavaException;
static VALUE
__allocate_JavaException(VALUE cause)
{
	JavaException *p = ALLOC(JavaException);
	if (p == NULL)
		rb_raise(rb_eNoMemError, "NoMemoryError");
	p->cause = cause;
	return Data_Wrap_Struct(r_JavaException, __mark_JavaException, __free_JavaException, p);
}
static VALUE
r_JavaException_new(VALUE self, VALUE cause)
{
	return __allocate_JavaException(cause);
}
static VALUE
r_JavaException_is$is_a(VALUE self, VALUE module)
{
	JavaException *p;
	Data_Get_Struct(self, JavaException, p);
	return rb_funcall(p->cause, rb_intern("is_a?"), 1, module);
}
static VALUE
r_JavaException_cause(VALUE self)
{
	JavaException *p;
	Data_Get_Struct(self, JavaException, p);
	return p->cause;
}
static VALUE
errinfo_getter(ID id)
{
	VALUE e = rb_gv_get("$!");
	if (TYPE(e) == T_DATA && RBASIC(e)->klass == r_JavaException) {
		JavaException *p;
		Data_Get_Struct(e, JavaException, p);
		return p->cause;
	}
	return e;
}
static VALUE r_JavaValue;
static VALUE
r_JavaValue_const_missing(VALUE self, VALUE symbol)
{
	if (TYPE(symbol) != T_SYMBOL)
		rb_raise(rb_eTypeError, "expected symbol");
	ID id = SYM2ID(symbol);
	return rb_const_get(rb_cObject, id);
}
static VALUE r_JavaBasicObject;
static VALUE
r_JavaBasicObject_eqv(VALUE self, VALUE o)
{
	if (TYPE(o) == T_DATA && RBASIC(o)->klass == r_JavaException) {
		JavaException *p;
		Data_Get_Struct(o, JavaException, p);
		return rb_obj_is_kind_of(p->cause, self);
	}
	return rb_obj_is_kind_of(o, self);
}
static VALUE r_JavaArray;
static VALUE
r_JavaArray_equals(VALUE self, VALUE o)
{
	return self == o ? Qtrue : Qfalse;
}
static VALUE
r_JavaArray_hashCode(VALUE self)
{
	return __allocate_Int32(__hash_int64((int64_t) self));
}
static VALUE
r_JavaArray_toString(VALUE self)
{
	return rb_funcall(rb_any_to_s(self), TO_J_ID, 0);
}
static VALUE
r_JavaArray_is$eql(VALUE self, VALUE o)
{
	return self == o ? Qtrue : Qfalse;
}
static VALUE
r_JavaArray_hash(VALUE self)
{
	return LONG2NUM((long) __hash_int64((int64_t) self));
}
static VALUE
r_JavaArray_to_s(VALUE self)
{
	return rb_any_to_s(self);
}
struct ObjectArray {
	int32_t length;
	VALUE data[];
};
typedef struct ObjectArray ObjectArray;
static VALUE r_ObjectArray;
struct BooleanArray {
	int32_t length;
	int8_t data[];
};
typedef struct BooleanArray BooleanArray;
static VALUE r_BooleanArray;
struct ByteArray {
	int32_t length;
	int8_t data[];
};
typedef struct ByteArray ByteArray;
static VALUE r_ByteArray;
struct CharArray {
	int32_t length;
	uint16_t data[];
};
typedef struct CharArray CharArray;
static VALUE r_CharArray;
struct Int16Array {
	int32_t length;
	int16_t data[];
};
typedef struct Int16Array Int16Array;
static VALUE r_Int16Array;
struct Int32Array {
	int32_t length;
	int32_t data[];
};
typedef struct Int32Array Int32Array;
static VALUE r_Int32Array;
struct Int64Array {
	int32_t length;
	int64_t data[];
};
typedef struct Int64Array Int64Array;
static VALUE r_Int64Array;
struct Float32Array {
	int32_t length;
	float data[];
};
typedef struct Float32Array Float32Array;
static VALUE r_Float32Array;
struct Float64Array {
	int32_t length;
	double data[];
};
typedef struct Float64Array Float64Array;
static VALUE r_Float64Array;
static VALUE
__outer_name(VALUE inner)
{
	ID id;
	VALUE name = rb_class_name(inner);
	long len = RSTRING_LEN(name);
	char *p = malloc(sizeof(char) * (size_t) (len + 2));
	if (p == NULL)
		rb_raise(rb_eNoMemError, "NoMemoryError");
	memcpy(p, RSTRING_PTR(name), (size_t) len);
	p[len++] = '[';
	p[len++] = ']';
	id = rb_intern2(p, len);
	free(p);
	return id;
}
static VALUE
__new_outer(VALUE inner)
{
	VALUE outer = rb_class_boot(r_ObjectArray);
	rb_undef_alloc_func(outer);
	rb_name_class(outer, __outer_name(inner));
	rb_singleton_class(outer);
	rb_ivar_set(outer, INNER_ID, inner);
	return outer;
}
static VALUE
__make_outer(VALUE inner)
{
	VALUE outer;
	outer = rb_ivar_get(inner, OUTER_ID);
	if (NIL_P(outer)) {
		outer = __new_outer(inner);
		rb_ivar_set(inner, OUTER_ID, outer);
	} else {
		__assert_class(outer);
	}
	return outer;
}
static void
__mark_ObjectArray(void *__p)
{
	ObjectArray *p = __p;
	int32_t i;
	for (i = 0; i < p->length; i += 1)
		rb_gc_mark(p->data[i]);
	return;
}
static void
__free_ObjectArray(void *__p)
{
	ObjectArray *p = __p;
	xfree(p);
	return;
}
static VALUE
__allocate_ObjectArray(VALUE klass, int32_t n, VALUE e)
{
	ObjectArray *p = xmalloc(sizeof(ObjectArray) + sizeof(VALUE) * (size_t) n);
	if (p == NULL)
		rb_raise(rb_eNoMemError, "NoMemoryError");
	p->length = n;
	int32_t i;
	for (i = 0; i < n; i += 1)
		p->data[i] = e;
	return Data_Wrap_Struct(klass, __mark_ObjectArray, __free_ObjectArray, p);
}
static VALUE
__allocate_BooleanArray(VALUE klass, int32_t n, int8_t e)
{
	BooleanArray *p = xmalloc(sizeof(BooleanArray) + sizeof(int8_t) * (size_t) n);
	if (p == NULL)
		rb_raise(rb_eNoMemError, "NoMemoryError");
	p->length = n;
	int32_t i;
	for (i = 0; i < n; i += 1)
		p->data[i] = e;
	return Data_Wrap_Struct(klass, NULL, NULL, p);
}
static VALUE
__allocate_ByteArray(VALUE klass, int32_t n, int8_t e)
{
	ByteArray *p = xmalloc(sizeof(ByteArray) + sizeof(int8_t) * (size_t) n);
	if (p == NULL)
		rb_raise(rb_eNoMemError, "NoMemoryError");
	p->length = n;
	int32_t i;
	for (i = 0; i < n; i += 1)
		p->data[i] = e;
	return Data_Wrap_Struct(klass, NULL, NULL, p);
}
static VALUE
__allocate_CharArray(VALUE klass, int32_t n, uint16_t e)
{
	CharArray *p = xmalloc(sizeof(CharArray) + sizeof(uint16_t) * (size_t) n);
	if (p == NULL)
		rb_raise(rb_eNoMemError, "NoMemoryError");
	p->length = n;
	int32_t i;
	for (i = 0; i < n; i += 1)
		p->data[i] = e;
	return Data_Wrap_Struct(klass, NULL, NULL, p);
}
static VALUE
__allocate_Int16Array(VALUE klass, int32_t n, int16_t e)
{
	Int16Array *p = xmalloc(sizeof(Int16Array) + sizeof(int16_t) * (size_t) n);
	if (p == NULL)
		rb_raise(rb_eNoMemError, "NoMemoryError");
	p->length = n;
	int32_t i;
	for (i = 0; i < n; i += 1)
		p->data[i] = e;
	return Data_Wrap_Struct(klass, NULL, NULL, p);
}
static VALUE
__allocate_Int32Array(VALUE klass, int32_t n, int32_t e)
{
	Int32Array *p = xmalloc(sizeof(Int32Array) + sizeof(int32_t) * (size_t) n);
	if (p == NULL)
		rb_raise(rb_eNoMemError, "NoMemoryError");
	p->length = n;
	int32_t i;
	for (i = 0; i < n; i += 1)
		p->data[i] = e;
	return Data_Wrap_Struct(klass, NULL, NULL, p);
}
static VALUE
__allocate_Int64Array(VALUE klass, int32_t n, int64_t e)
{
	Int64Array *p = xmalloc(sizeof(Int64Array) + sizeof(int64_t) * (size_t) n);
	if (p == NULL)
		rb_raise(rb_eNoMemError, "NoMemoryError");
	p->length = n;
	int32_t i;
	for (i = 0; i < n; i += 1)
		p->data[i] = e;
	return Data_Wrap_Struct(klass, NULL, NULL, p);
}
static VALUE
__allocate_Float32Array(VALUE klass, int32_t n, float e)
{
	Float32Array *p = xmalloc(sizeof(Float32Array) + sizeof(float) * (size_t) n);
	if (p == NULL)
		rb_raise(rb_eNoMemError, "NoMemoryError");
	p->length = n;
	int32_t i;
	for (i = 0; i < n; i += 1)
		p->data[i] = e;
	return Data_Wrap_Struct(klass, NULL, NULL, p);
}
static VALUE
__allocate_Float64Array(VALUE klass, int32_t n, double e)
{
	Float64Array *p = xmalloc(sizeof(Float64Array) + sizeof(double) * (size_t) n);
	if (p == NULL)
		rb_raise(rb_eNoMemError, "NoMemoryError");
	p->length = n;
	int32_t i;
	for (i = 0; i < n; i += 1)
		p->data[i] = e;
	return Data_Wrap_Struct(klass, NULL, NULL, p);
}
static void
__arraycopy_ObjectArray(ObjectArray *p0, int32_t i0, ObjectArray *p1, int32_t i1, int32_t n)
{
	int32_t j0 = i0 + n;
	int32_t j1 = i1 + n;
	if (j0 < 0 || j0 > p0->length)
		rb_raise(rb_eArgError, "index out of bounds");
	if (j1 < 0 || j1 > p1->length)
		rb_raise(rb_eArgError, "index out of bounds");
	while (n-- > 0)
		p1->data[i1++] = p0->data[i0++];
	return;
}
static void
__arraycopy_BooleanArray(BooleanArray *p0, int32_t i0, BooleanArray *p1, int32_t i1, int32_t n)
{
	int32_t j0 = i0 + n;
	int32_t j1 = i1 + n;
	if (j0 < 0 || j0 > p0->length)
		rb_raise(rb_eArgError, "index out of bounds");
	if (j1 < 0 || j1 > p1->length)
		rb_raise(rb_eArgError, "index out of bounds");
	while (n-- > 0)
		p1->data[i1++] = p0->data[i0++];
	return;
}
static void
__arraycopy_ByteArray(ByteArray *p0, int32_t i0, ByteArray *p1, int32_t i1, int32_t n)
{
	int32_t j0 = i0 + n;
	int32_t j1 = i1 + n;
	if (j0 < 0 || j0 > p0->length)
		rb_raise(rb_eArgError, "index out of bounds");
	if (j1 < 0 || j1 > p1->length)
		rb_raise(rb_eArgError, "index out of bounds");
	while (n-- > 0)
		p1->data[i1++] = p0->data[i0++];
	return;
}
static void
__arraycopy_CharArray(CharArray *p0, int32_t i0, CharArray *p1, int32_t i1, int32_t n)
{
	int32_t j0 = i0 + n;
	int32_t j1 = i1 + n;
	if (j0 < 0 || j0 > p0->length)
		rb_raise(rb_eArgError, "index out of bounds");
	if (j1 < 0 || j1 > p1->length)
		rb_raise(rb_eArgError, "index out of bounds");
	while (n-- > 0)
		p1->data[i1++] = p0->data[i0++];
	return;
}
static void
__arraycopy_Int16Array(Int16Array *p0, int32_t i0, Int16Array *p1, int32_t i1, int32_t n)
{
	int32_t j0 = i0 + n;
	int32_t j1 = i1 + n;
	if (j0 < 0 || j0 > p0->length)
		rb_raise(rb_eArgError, "index out of bounds");
	if (j1 < 0 || j1 > p1->length)
		rb_raise(rb_eArgError, "index out of bounds");
	while (n-- > 0)
		p1->data[i1++] = p0->data[i0++];
	return;
}
static void
__arraycopy_Int32Array(Int32Array *p0, int32_t i0, Int32Array *p1, int32_t i1, int32_t n)
{
	int32_t j0 = i0 + n;
	int32_t j1 = i1 + n;
	if (j0 < 0 || j0 > p0->length)
		rb_raise(rb_eArgError, "index out of bounds");
	if (j1 < 0 || j1 > p1->length)
		rb_raise(rb_eArgError, "index out of bounds");
	while (n-- > 0)
		p1->data[i1++] = p0->data[i0++];
	return;
}
static void
__arraycopy_Int64Array(Int64Array *p0, int32_t i0, Int64Array *p1, int32_t i1, int32_t n)
{
	int32_t j0 = i0 + n;
	int32_t j1 = i1 + n;
	if (j0 < 0 || j0 > p0->length)
		rb_raise(rb_eArgError, "index out of bounds");
	if (j1 < 0 || j1 > p1->length)
		rb_raise(rb_eArgError, "index out of bounds");
	while (n-- > 0)
		p1->data[i1++] = p0->data[i0++];
	return;
}
static void
__arraycopy_Float32Array(Float32Array *p0, int32_t i0, Float32Array *p1, int32_t i1, int32_t n)
{
	int32_t j0 = i0 + n;
	int32_t j1 = i1 + n;
	if (j0 < 0 || j0 > p0->length)
		rb_raise(rb_eArgError, "index out of bounds");
	if (j1 < 0 || j1 > p1->length)
		rb_raise(rb_eArgError, "index out of bounds");
	while (n-- > 0)
		p1->data[i1++] = p0->data[i0++];
	return;
}
static void
__arraycopy_Float64Array(Float64Array *p0, int32_t i0, Float64Array *p1, int32_t i1, int32_t n)
{
	int32_t j0 = i0 + n;
	int32_t j1 = i1 + n;
	if (j0 < 0 || j0 > p0->length)
		rb_raise(rb_eArgError, "index out of bounds");
	if (j1 < 0 || j1 > p1->length)
		rb_raise(rb_eArgError, "index out of bounds");
	while (n-- > 0)
		p1->data[i1++] = p0->data[i0++];
	return;
}
static VALUE
r_ObjectArray_new(int argc, VALUE *argv, VALUE self)
{
	if (argc == 0) {
		ObjectArray *p = xmalloc(sizeof(ObjectArray));
		if (p == NULL)
			rb_raise(rb_eNoMemError, "NoMemoryError");
		p->length = 0;
		return Data_Wrap_Struct(self, __mark_ObjectArray, __free_ObjectArray, p);
	}
	if (argc == 1) {
		int32_t n = __accept_natural_int32(argv[0]);
		return __allocate_ObjectArray(self, n, Qnil);
	}
	if (argc == 2) {
		int32_t n = __accept_natural_int32(argv[0]);
		return __allocate_ObjectArray(self, n, argv[1]);
	}
	rb_raise(rb_eArgError, "wrong number of arguments");
}
static VALUE
r_ObjectArray_of(VALUE self, VALUE args)
{
	long len = RARRAY_LEN(args);
	if (len > 0x000000007FFFFFFFL)
		rb_raise(rb_eRangeError, "too many arguments");
	int32_t n = (int32_t) len;
	ObjectArray *p = xmalloc(sizeof(ObjectArray) + sizeof(VALUE) * (size_t) n);
	if (p == NULL)
		rb_raise(rb_eNoMemError, "NoMemoryError");
	p->length = n;
	VALUE *values = RARRAY_PTR(args);
	int32_t i;
	for (i = 0; i < n; i += 1) {
		VALUE e = values[i];
		p->data[i] = e;
	}
	return Data_Wrap_Struct(self, __mark_ObjectArray, __free_ObjectArray, p);
}
static VALUE
r_BooleanArray_new(int argc, VALUE *argv, VALUE self)
{
	if (argc == 0) {
		BooleanArray *p = xmalloc(sizeof(BooleanArray));
		if (p == NULL)
			rb_raise(rb_eNoMemError, "NoMemoryError");
		p->length = 0;
		return Data_Wrap_Struct(self, NULL, NULL, p);
	}
	if (argc == 1) {
		int32_t n = __accept_natural_int32(argv[0]);
		return __allocate_BooleanArray(self, n, 0);
	}
	if (argc == 2) {
		int32_t n = __accept_natural_int32(argv[0]);
		return __allocate_BooleanArray(self, n, __accept_boolean(argv[1]));
	}
	rb_raise(rb_eArgError, "wrong number of arguments");
}
static VALUE
r_BooleanArray_of(VALUE self, VALUE args)
{
	long len = RARRAY_LEN(args);
	if (len > 0x000000007FFFFFFFL)
		rb_raise(rb_eRangeError, "too many arguments");
	int32_t n = (int32_t) len;
	BooleanArray *p = xmalloc(sizeof(BooleanArray) + sizeof(int8_t) * (size_t) n);
	if (p == NULL)
		rb_raise(rb_eNoMemError, "NoMemoryError");
	p->length = n;
	VALUE *values = RARRAY_PTR(args);
	int32_t i;
	for (i = 0; i < n; i += 1) {
		VALUE e = values[i];
		p->data[i] = __accept_boolean(e);
	}
	return Data_Wrap_Struct(self, NULL, NULL, p);
}
static VALUE
r_ByteArray_new(int argc, VALUE *argv, VALUE self)
{
	if (argc == 0) {
		ByteArray *p = xmalloc(sizeof(ByteArray));
		if (p == NULL)
			rb_raise(rb_eNoMemError, "NoMemoryError");
		p->length = 0;
		return Data_Wrap_Struct(self, NULL, NULL, p);
	}
	if (argc == 1) {
		int32_t n = __accept_natural_int32(argv[0]);
		return __allocate_ByteArray(self, n, 0);
	}
	if (argc == 2) {
		int32_t n = __accept_natural_int32(argv[0]);
		return __allocate_ByteArray(self, n, __accept_byte(argv[1]));
	}
	rb_raise(rb_eArgError, "wrong number of arguments");
}
static VALUE
r_ByteArray_of(VALUE self, VALUE args)
{
	long len = RARRAY_LEN(args);
	if (len > 0x000000007FFFFFFFL)
		rb_raise(rb_eRangeError, "too many arguments");
	int32_t n = (int32_t) len;
	ByteArray *p = xmalloc(sizeof(ByteArray) + sizeof(int8_t) * (size_t) n);
	if (p == NULL)
		rb_raise(rb_eNoMemError, "NoMemoryError");
	p->length = n;
	VALUE *values = RARRAY_PTR(args);
	int32_t i;
	for (i = 0; i < n; i += 1) {
		VALUE e = values[i];
		p->data[i] = __accept_byte(e);
	}
	return Data_Wrap_Struct(self, NULL, NULL, p);
}
static VALUE
r_CharArray_new(int argc, VALUE *argv, VALUE self)
{
	if (argc == 0) {
		CharArray *p = xmalloc(sizeof(CharArray));
		if (p == NULL)
			rb_raise(rb_eNoMemError, "NoMemoryError");
		p->length = 0;
		return Data_Wrap_Struct(self, NULL, NULL, p);
	}
	if (argc == 1) {
		int32_t n = __accept_natural_int32(argv[0]);
		return __allocate_CharArray(self, n, 0);
	}
	if (argc == 2) {
		int32_t n = __accept_natural_int32(argv[0]);
		return __allocate_CharArray(self, n, __accept_char(argv[1]));
	}
	rb_raise(rb_eArgError, "wrong number of arguments");
}
static VALUE
r_CharArray_of(VALUE self, VALUE args)
{
	long len = RARRAY_LEN(args);
	if (len > 0x000000007FFFFFFFL)
		rb_raise(rb_eRangeError, "too many arguments");
	int32_t n = (int32_t) len;
	CharArray *p = xmalloc(sizeof(CharArray) + sizeof(uint16_t) * (size_t) n);
	if (p == NULL)
		rb_raise(rb_eNoMemError, "NoMemoryError");
	p->length = n;
	VALUE *values = RARRAY_PTR(args);
	int32_t i;
	for (i = 0; i < n; i += 1) {
		VALUE e = values[i];
		p->data[i] = __accept_char(e);
	}
	return Data_Wrap_Struct(self, NULL, NULL, p);
}
static VALUE
r_Int16Array_new(int argc, VALUE *argv, VALUE self)
{
	if (argc == 0) {
		Int16Array *p = xmalloc(sizeof(Int16Array));
		if (p == NULL)
			rb_raise(rb_eNoMemError, "NoMemoryError");
		p->length = 0;
		return Data_Wrap_Struct(self, NULL, NULL, p);
	}
	if (argc == 1) {
		int32_t n = __accept_natural_int32(argv[0]);
		return __allocate_Int16Array(self, n, 0);
	}
	if (argc == 2) {
		int32_t n = __accept_natural_int32(argv[0]);
		return __allocate_Int16Array(self, n, __accept_int16(argv[1]));
	}
	rb_raise(rb_eArgError, "wrong number of arguments");
}
static VALUE
r_Int16Array_of(VALUE self, VALUE args)
{
	long len = RARRAY_LEN(args);
	if (len > 0x000000007FFFFFFFL)
		rb_raise(rb_eRangeError, "too many arguments");
	int32_t n = (int32_t) len;
	Int16Array *p = xmalloc(sizeof(Int16Array) + sizeof(int16_t) * (size_t) n);
	if (p == NULL)
		rb_raise(rb_eNoMemError, "NoMemoryError");
	p->length = n;
	VALUE *values = RARRAY_PTR(args);
	int32_t i;
	for (i = 0; i < n; i += 1) {
		VALUE e = values[i];
		p->data[i] = __accept_int16(e);
	}
	return Data_Wrap_Struct(self, NULL, NULL, p);
}
static VALUE
r_Int32Array_new(int argc, VALUE *argv, VALUE self)
{
	if (argc == 0) {
		Int32Array *p = xmalloc(sizeof(Int32Array));
		if (p == NULL)
			rb_raise(rb_eNoMemError, "NoMemoryError");
		p->length = 0;
		return Data_Wrap_Struct(self, NULL, NULL, p);
	}
	if (argc == 1) {
		int32_t n = __accept_natural_int32(argv[0]);
		return __allocate_Int32Array(self, n, 0);
	}
	if (argc == 2) {
		int32_t n = __accept_natural_int32(argv[0]);
		return __allocate_Int32Array(self, n, __accept_int32(argv[1]));
	}
	rb_raise(rb_eArgError, "wrong number of arguments");
}
static VALUE
r_Int32Array_of(VALUE self, VALUE args)
{
	long len = RARRAY_LEN(args);
	if (len > 0x000000007FFFFFFFL)
		rb_raise(rb_eRangeError, "too many arguments");
	int32_t n = (int32_t) len;
	Int32Array *p = xmalloc(sizeof(Int32Array) + sizeof(int32_t) * (size_t) n);
	if (p == NULL)
		rb_raise(rb_eNoMemError, "NoMemoryError");
	p->length = n;
	VALUE *values = RARRAY_PTR(args);
	int32_t i;
	for (i = 0; i < n; i += 1) {
		VALUE e = values[i];
		p->data[i] = __accept_int32(e);
	}
	return Data_Wrap_Struct(self, NULL, NULL, p);
}
static VALUE
r_Int64Array_new(int argc, VALUE *argv, VALUE self)
{
	if (argc == 0) {
		Int64Array *p = xmalloc(sizeof(Int64Array));
		if (p == NULL)
			rb_raise(rb_eNoMemError, "NoMemoryError");
		p->length = 0;
		return Data_Wrap_Struct(self, NULL, NULL, p);
	}
	if (argc == 1) {
		int32_t n = __accept_natural_int32(argv[0]);
		return __allocate_Int64Array(self, n, 0);
	}
	if (argc == 2) {
		int32_t n = __accept_natural_int32(argv[0]);
		return __allocate_Int64Array(self, n, __accept_int64(argv[1]));
	}
	rb_raise(rb_eArgError, "wrong number of arguments");
}
static VALUE
r_Int64Array_of(VALUE self, VALUE args)
{
	long len = RARRAY_LEN(args);
	if (len > 0x000000007FFFFFFFL)
		rb_raise(rb_eRangeError, "too many arguments");
	int32_t n = (int32_t) len;
	Int64Array *p = xmalloc(sizeof(Int64Array) + sizeof(int64_t) * (size_t) n);
	if (p == NULL)
		rb_raise(rb_eNoMemError, "NoMemoryError");
	p->length = n;
	VALUE *values = RARRAY_PTR(args);
	int32_t i;
	for (i = 0; i < n; i += 1) {
		VALUE e = values[i];
		p->data[i] = __accept_int64(e);
	}
	return Data_Wrap_Struct(self, NULL, NULL, p);
}
static VALUE
r_Float32Array_new(int argc, VALUE *argv, VALUE self)
{
	if (argc == 0) {
		Float32Array *p = xmalloc(sizeof(Float32Array));
		if (p == NULL)
			rb_raise(rb_eNoMemError, "NoMemoryError");
		p->length = 0;
		return Data_Wrap_Struct(self, NULL, NULL, p);
	}
	if (argc == 1) {
		int32_t n = __accept_natural_int32(argv[0]);
		return __allocate_Float32Array(self, n, 0);
	}
	if (argc == 2) {
		int32_t n = __accept_natural_int32(argv[0]);
		return __allocate_Float32Array(self, n, __accept_float32(argv[1]));
	}
	rb_raise(rb_eArgError, "wrong number of arguments");
}
static VALUE
r_Float32Array_of(VALUE self, VALUE args)
{
	long len = RARRAY_LEN(args);
	if (len > 0x000000007FFFFFFFL)
		rb_raise(rb_eRangeError, "too many arguments");
	int32_t n = (int32_t) len;
	Float32Array *p = xmalloc(sizeof(Float32Array) + sizeof(float) * (size_t) n);
	if (p == NULL)
		rb_raise(rb_eNoMemError, "NoMemoryError");
	p->length = n;
	VALUE *values = RARRAY_PTR(args);
	int32_t i;
	for (i = 0; i < n; i += 1) {
		VALUE e = values[i];
		p->data[i] = __accept_float32(e);
	}
	return Data_Wrap_Struct(self, NULL, NULL, p);
}
static VALUE
r_Float64Array_new(int argc, VALUE *argv, VALUE self)
{
	if (argc == 0) {
		Float64Array *p = xmalloc(sizeof(Float64Array));
		if (p == NULL)
			rb_raise(rb_eNoMemError, "NoMemoryError");
		p->length = 0;
		return Data_Wrap_Struct(self, NULL, NULL, p);
	}
	if (argc == 1) {
		int32_t n = __accept_natural_int32(argv[0]);
		return __allocate_Float64Array(self, n, 0);
	}
	if (argc == 2) {
		int32_t n = __accept_natural_int32(argv[0]);
		return __allocate_Float64Array(self, n, __accept_float64(argv[1]));
	}
	rb_raise(rb_eArgError, "wrong number of arguments");
}
static VALUE
r_Float64Array_of(VALUE self, VALUE args)
{
	long len = RARRAY_LEN(args);
	if (len > 0x000000007FFFFFFFL)
		rb_raise(rb_eRangeError, "too many arguments");
	int32_t n = (int32_t) len;
	Float64Array *p = xmalloc(sizeof(Float64Array) + sizeof(double) * (size_t) n);
	if (p == NULL)
		rb_raise(rb_eNoMemError, "NoMemoryError");
	p->length = n;
	VALUE *values = RARRAY_PTR(args);
	int32_t i;
	for (i = 0; i < n; i += 1) {
		VALUE e = values[i];
		p->data[i] = __accept_float64(e);
	}
	return Data_Wrap_Struct(self, NULL, NULL, p);
}
static VALUE
r_ObjectArray_clone(VALUE self)
{
	ObjectArray *p0;
	Data_Get_Struct(self, ObjectArray, p0);
	int32_t n = p0->length;
	ObjectArray *p1 = xmalloc(sizeof(ObjectArray) + sizeof(VALUE) * (size_t) n);
	if (p1 == NULL)
		rb_raise(rb_eNoMemError, "NoMemoryError");
	p1->length = n;
	int32_t i;
	for (i = 0; i < n; i += 1)
		p1->data[i] = p0->data[i];
	return Data_Wrap_Struct(RBASIC(self)->klass, __mark_ObjectArray, __free_ObjectArray, p1);
}
static VALUE
r_ObjectArray_length(VALUE self)
{
	ObjectArray *p;
	Data_Get_Struct(self, ObjectArray, p);
	return __allocate_Int32(p->length);
}
static VALUE
r_ObjectArray_get(VALUE self, VALUE index)
{
	ObjectArray *p;
	Data_Get_Struct(self, ObjectArray, p);
	int32_t i = __accept_bounded_int32(index, p->length);
	return p->data[i];
}
static VALUE
r_ObjectArray_set(VALUE self, VALUE index, VALUE e)
{
	ObjectArray *p;
	Data_Get_Struct(self, ObjectArray, p);
	int32_t i = __accept_bounded_int32(index, p->length);
	p->data[i] = e;
	return Qnil;
}
static VALUE
r_ObjectArray_copy(VALUE self, VALUE i0, VALUE m1, VALUE i1, VALUE n)
{
	ObjectArray *p0;
	Data_Get_Struct(self, ObjectArray, p0);
	if (TYPE(m1) == T_DATA && rb_class_inherited_p(RBASIC(m1)->klass, r_ObjectArray) == Qtrue) {
		ObjectArray *p1;
		Data_Get_Struct(m1, ObjectArray, p1);
		__arraycopy_ObjectArray(p0, __accept_natural_int32(i0), p1, __accept_natural_int32(i1), __accept_natural_int32(n));
		return Qnil;
	}
	rb_raise(rb_eTypeError, "expected array");
}
static VALUE
r_ObjectArray_each(VALUE self)
{
	ObjectArray *p;
	Data_Get_Struct(self, ObjectArray, p);
	int32_t i = 0;
	while (i < p->length) {
		rb_yield(p->data[i]);
		i += 1;
	}
	return Qnil;
}
static VALUE
r_BooleanArray_clone(VALUE self)
{
	BooleanArray *p0;
	Data_Get_Struct(self, BooleanArray, p0);
	int32_t n = p0->length;
	BooleanArray *p1 = xmalloc(sizeof(BooleanArray) + sizeof(int8_t) * (size_t) n);
	if (p1 == NULL)
		rb_raise(rb_eNoMemError, "NoMemoryError");
	p1->length = n;
	int32_t i;
	for (i = 0; i < n; i += 1)
		p1->data[i] = p0->data[i];
	return Data_Wrap_Struct(RBASIC(self)->klass, NULL, NULL, p1);
}
static VALUE
r_BooleanArray_length(VALUE self)
{
	BooleanArray *p;
	Data_Get_Struct(self, BooleanArray, p);
	return __allocate_Int32(p->length);
}
static VALUE
r_BooleanArray_get(VALUE self, VALUE index)
{
	BooleanArray *p;
	Data_Get_Struct(self, BooleanArray, p);
	int32_t i = __accept_bounded_int32(index, p->length);
	return p->data[i] != 0 ? Qtrue : Qfalse;
}
static VALUE
r_BooleanArray_set(VALUE self, VALUE index, VALUE e)
{
	BooleanArray *p;
	Data_Get_Struct(self, BooleanArray, p);
	int32_t i = __accept_bounded_int32(index, p->length);
	p->data[i] = __accept_boolean(e);
	return Qnil;
}
static VALUE
r_BooleanArray_copy(VALUE self, VALUE i0, VALUE m1, VALUE i1, VALUE n)
{
	BooleanArray *p0;
	Data_Get_Struct(self, BooleanArray, p0);
	if (TYPE(m1) == T_DATA && RBASIC(m1)->klass == r_BooleanArray) {
		BooleanArray *p1;
		Data_Get_Struct(m1, BooleanArray, p1);
		__arraycopy_BooleanArray(p0, __accept_natural_int32(i0), p1, __accept_natural_int32(i1), __accept_natural_int32(n));
		return Qnil;
	}
	rb_raise(rb_eTypeError, "expected array");
}
static VALUE
r_BooleanArray_each(VALUE self)
{
	BooleanArray *p;
	Data_Get_Struct(self, BooleanArray, p);
	int32_t i = 0;
	while (i < p->length) {
		rb_yield(p->data[i] != 0 ? Qtrue : Qfalse);
		i += 1;
	}
	return Qnil;
}
static VALUE
r_ByteArray_to_s(VALUE self)
{
	ByteArray *p;
	Data_Get_Struct(self, ByteArray, p);
	return rb_str_new((char*) p->data, p->length);
}
static VALUE
r_ByteArray_clone(VALUE self)
{
	ByteArray *p0;
	Data_Get_Struct(self, ByteArray, p0);
	int32_t n = p0->length;
	ByteArray *p1 = xmalloc(sizeof(ByteArray) + sizeof(int8_t) * (size_t) n);
	if (p1 == NULL)
		rb_raise(rb_eNoMemError, "NoMemoryError");
	p1->length = n;
	int32_t i;
	for (i = 0; i < n; i += 1)
		p1->data[i] = p0->data[i];
	return Data_Wrap_Struct(RBASIC(self)->klass, NULL, NULL, p1);
}
static VALUE
r_ByteArray_length(VALUE self)
{
	ByteArray *p;
	Data_Get_Struct(self, ByteArray, p);
	return __allocate_Int32(p->length);
}
static VALUE
r_ByteArray_get(VALUE self, VALUE index)
{
	ByteArray *p;
	Data_Get_Struct(self, ByteArray, p);
	int32_t i = __accept_bounded_int32(index, p->length);
	return __allocate_Byte(p->data[i]);
}
static VALUE
r_ByteArray_set(VALUE self, VALUE index, VALUE e)
{
	ByteArray *p;
	Data_Get_Struct(self, ByteArray, p);
	int32_t i = __accept_bounded_int32(index, p->length);
	p->data[i] = __accept_byte(e);
	return Qnil;
}
static VALUE
r_ByteArray_copy(VALUE self, VALUE i0, VALUE m1, VALUE i1, VALUE n)
{
	ByteArray *p0;
	Data_Get_Struct(self, ByteArray, p0);
	if (TYPE(m1) == T_DATA && RBASIC(m1)->klass == r_ByteArray) {
		ByteArray *p1;
		Data_Get_Struct(m1, ByteArray, p1);
		__arraycopy_ByteArray(p0, __accept_natural_int32(i0), p1, __accept_natural_int32(i1), __accept_natural_int32(n));
		return Qnil;
	}
	rb_raise(rb_eTypeError, "expected array");
}
static VALUE
r_ByteArray_each(VALUE self)
{
	ByteArray *p;
	Data_Get_Struct(self, ByteArray, p);
	int32_t i = 0;
	while (i < p->length) {
		rb_yield(__allocate_Byte(p->data[i]));
		i += 1;
	}
	return Qnil;
}
static VALUE
r_CharArray_clone(VALUE self)
{
	CharArray *p0;
	Data_Get_Struct(self, CharArray, p0);
	int32_t n = p0->length;
	CharArray *p1 = xmalloc(sizeof(CharArray) + sizeof(uint16_t) * (size_t) n);
	if (p1 == NULL)
		rb_raise(rb_eNoMemError, "NoMemoryError");
	p1->length = n;
	int32_t i;
	for (i = 0; i < n; i += 1)
		p1->data[i] = p0->data[i];
	return Data_Wrap_Struct(RBASIC(self)->klass, NULL, NULL, p1);
}
static VALUE
r_CharArray_length(VALUE self)
{
	CharArray *p;
	Data_Get_Struct(self, CharArray, p);
	return __allocate_Int32(p->length);
}
static VALUE
r_CharArray_get(VALUE self, VALUE index)
{
	CharArray *p;
	Data_Get_Struct(self, CharArray, p);
	int32_t i = __accept_bounded_int32(index, p->length);
	return __allocate_Char(p->data[i]);
}
static VALUE
r_CharArray_set(VALUE self, VALUE index, VALUE e)
{
	CharArray *p;
	Data_Get_Struct(self, CharArray, p);
	int32_t i = __accept_bounded_int32(index, p->length);
	p->data[i] = __accept_char(e);
	return Qnil;
}
static VALUE
r_CharArray_copy(VALUE self, VALUE i0, VALUE m1, VALUE i1, VALUE n)
{
	CharArray *p0;
	Data_Get_Struct(self, CharArray, p0);
	if (TYPE(m1) == T_DATA && RBASIC(m1)->klass == r_CharArray) {
		CharArray *p1;
		Data_Get_Struct(m1, CharArray, p1);
		__arraycopy_CharArray(p0, __accept_natural_int32(i0), p1, __accept_natural_int32(i1), __accept_natural_int32(n));
		return Qnil;
	}
	rb_raise(rb_eTypeError, "expected array");
}
static VALUE
r_CharArray_each(VALUE self)
{
	CharArray *p;
	Data_Get_Struct(self, CharArray, p);
	int32_t i = 0;
	while (i < p->length) {
		rb_yield(__allocate_Char(p->data[i]));
		i += 1;
	}
	return Qnil;
}
static VALUE
r_Int16Array_clone(VALUE self)
{
	Int16Array *p0;
	Data_Get_Struct(self, Int16Array, p0);
	int32_t n = p0->length;
	Int16Array *p1 = xmalloc(sizeof(Int16Array) + sizeof(int16_t) * (size_t) n);
	if (p1 == NULL)
		rb_raise(rb_eNoMemError, "NoMemoryError");
	p1->length = n;
	int32_t i;
	for (i = 0; i < n; i += 1)
		p1->data[i] = p0->data[i];
	return Data_Wrap_Struct(RBASIC(self)->klass, NULL, NULL, p1);
}
static VALUE
r_Int16Array_length(VALUE self)
{
	Int16Array *p;
	Data_Get_Struct(self, Int16Array, p);
	return __allocate_Int32(p->length);
}
static VALUE
r_Int16Array_get(VALUE self, VALUE index)
{
	Int16Array *p;
	Data_Get_Struct(self, Int16Array, p);
	int32_t i = __accept_bounded_int32(index, p->length);
	return __allocate_Int16(p->data[i]);
}
static VALUE
r_Int16Array_set(VALUE self, VALUE index, VALUE e)
{
	Int16Array *p;
	Data_Get_Struct(self, Int16Array, p);
	int32_t i = __accept_bounded_int32(index, p->length);
	p->data[i] = __accept_int16(e);
	return Qnil;
}
static VALUE
r_Int16Array_copy(VALUE self, VALUE i0, VALUE m1, VALUE i1, VALUE n)
{
	Int16Array *p0;
	Data_Get_Struct(self, Int16Array, p0);
	if (TYPE(m1) == T_DATA && RBASIC(m1)->klass == r_Int16Array) {
		Int16Array *p1;
		Data_Get_Struct(m1, Int16Array, p1);
		__arraycopy_Int16Array(p0, __accept_natural_int32(i0), p1, __accept_natural_int32(i1), __accept_natural_int32(n));
		return Qnil;
	}
	rb_raise(rb_eTypeError, "expected array");
}
static VALUE
r_Int16Array_each(VALUE self)
{
	Int16Array *p;
	Data_Get_Struct(self, Int16Array, p);
	int32_t i = 0;
	while (i < p->length) {
		rb_yield(__allocate_Int16(p->data[i]));
		i += 1;
	}
	return Qnil;
}
static VALUE
r_Int32Array_clone(VALUE self)
{
	Int32Array *p0;
	Data_Get_Struct(self, Int32Array, p0);
	int32_t n = p0->length;
	Int32Array *p1 = xmalloc(sizeof(Int32Array) + sizeof(int32_t) * (size_t) n);
	if (p1 == NULL)
		rb_raise(rb_eNoMemError, "NoMemoryError");
	p1->length = n;
	int32_t i;
	for (i = 0; i < n; i += 1)
		p1->data[i] = p0->data[i];
	return Data_Wrap_Struct(RBASIC(self)->klass, NULL, NULL, p1);
}
static VALUE
r_Int32Array_length(VALUE self)
{
	Int32Array *p;
	Data_Get_Struct(self, Int32Array, p);
	return __allocate_Int32(p->length);
}
static VALUE
r_Int32Array_get(VALUE self, VALUE index)
{
	Int32Array *p;
	Data_Get_Struct(self, Int32Array, p);
	int32_t i = __accept_bounded_int32(index, p->length);
	return __allocate_Int32(p->data[i]);
}
static VALUE
r_Int32Array_set(VALUE self, VALUE index, VALUE e)
{
	Int32Array *p;
	Data_Get_Struct(self, Int32Array, p);
	int32_t i = __accept_bounded_int32(index, p->length);
	p->data[i] = __accept_int32(e);
	return Qnil;
}
static VALUE
r_Int32Array_copy(VALUE self, VALUE i0, VALUE m1, VALUE i1, VALUE n)
{
	Int32Array *p0;
	Data_Get_Struct(self, Int32Array, p0);
	if (TYPE(m1) == T_DATA && RBASIC(m1)->klass == r_Int32Array) {
		Int32Array *p1;
		Data_Get_Struct(m1, Int32Array, p1);
		__arraycopy_Int32Array(p0, __accept_natural_int32(i0), p1, __accept_natural_int32(i1), __accept_natural_int32(n));
		return Qnil;
	}
	rb_raise(rb_eTypeError, "expected array");
}
static VALUE
r_Int32Array_each(VALUE self)
{
	Int32Array *p;
	Data_Get_Struct(self, Int32Array, p);
	int32_t i = 0;
	while (i < p->length) {
		rb_yield(__allocate_Int32(p->data[i]));
		i += 1;
	}
	return Qnil;
}
static VALUE
r_Int64Array_clone(VALUE self)
{
	Int64Array *p0;
	Data_Get_Struct(self, Int64Array, p0);
	int32_t n = p0->length;
	Int64Array *p1 = xmalloc(sizeof(Int64Array) + sizeof(int64_t) * (size_t) n);
	if (p1 == NULL)
		rb_raise(rb_eNoMemError, "NoMemoryError");
	p1->length = n;
	int32_t i;
	for (i = 0; i < n; i += 1)
		p1->data[i] = p0->data[i];
	return Data_Wrap_Struct(RBASIC(self)->klass, NULL, NULL, p1);
}
static VALUE
r_Int64Array_length(VALUE self)
{
	Int64Array *p;
	Data_Get_Struct(self, Int64Array, p);
	return __allocate_Int32(p->length);
}
static VALUE
r_Int64Array_get(VALUE self, VALUE index)
{
	Int64Array *p;
	Data_Get_Struct(self, Int64Array, p);
	int32_t i = __accept_bounded_int32(index, p->length);
	return __allocate_Int64(p->data[i]);
}
static VALUE
r_Int64Array_set(VALUE self, VALUE index, VALUE e)
{
	Int64Array *p;
	Data_Get_Struct(self, Int64Array, p);
	int32_t i = __accept_bounded_int32(index, p->length);
	p->data[i] = __accept_int64(e);
	return Qnil;
}
static VALUE
r_Int64Array_copy(VALUE self, VALUE i0, VALUE m1, VALUE i1, VALUE n)
{
	Int64Array *p0;
	Data_Get_Struct(self, Int64Array, p0);
	if (TYPE(m1) == T_DATA && RBASIC(m1)->klass == r_Int64Array) {
		Int64Array *p1;
		Data_Get_Struct(m1, Int64Array, p1);
		__arraycopy_Int64Array(p0, __accept_natural_int32(i0), p1, __accept_natural_int32(i1), __accept_natural_int32(n));
		return Qnil;
	}
	rb_raise(rb_eTypeError, "expected array");
}
static VALUE
r_Int64Array_each(VALUE self)
{
	Int64Array *p;
	Data_Get_Struct(self, Int64Array, p);
	int32_t i = 0;
	while (i < p->length) {
		rb_yield(__allocate_Int64(p->data[i]));
		i += 1;
	}
	return Qnil;
}
static VALUE
r_Float32Array_clone(VALUE self)
{
	Float32Array *p0;
	Data_Get_Struct(self, Float32Array, p0);
	int32_t n = p0->length;
	Float32Array *p1 = xmalloc(sizeof(Float32Array) + sizeof(float) * (size_t) n);
	if (p1 == NULL)
		rb_raise(rb_eNoMemError, "NoMemoryError");
	p1->length = n;
	int32_t i;
	for (i = 0; i < n; i += 1)
		p1->data[i] = p0->data[i];
	return Data_Wrap_Struct(RBASIC(self)->klass, NULL, NULL, p1);
}
static VALUE
r_Float32Array_length(VALUE self)
{
	Float32Array *p;
	Data_Get_Struct(self, Float32Array, p);
	return __allocate_Int32(p->length);
}
static VALUE
r_Float32Array_get(VALUE self, VALUE index)
{
	Float32Array *p;
	Data_Get_Struct(self, Float32Array, p);
	int32_t i = __accept_bounded_int32(index, p->length);
	return __allocate_Float32(p->data[i]);
}
static VALUE
r_Float32Array_set(VALUE self, VALUE index, VALUE e)
{
	Float32Array *p;
	Data_Get_Struct(self, Float32Array, p);
	int32_t i = __accept_bounded_int32(index, p->length);
	p->data[i] = __accept_float32(e);
	return Qnil;
}
static VALUE
r_Float32Array_copy(VALUE self, VALUE i0, VALUE m1, VALUE i1, VALUE n)
{
	Float32Array *p0;
	Data_Get_Struct(self, Float32Array, p0);
	if (TYPE(m1) == T_DATA && RBASIC(m1)->klass == r_Float32Array) {
		Float32Array *p1;
		Data_Get_Struct(m1, Float32Array, p1);
		__arraycopy_Float32Array(p0, __accept_natural_int32(i0), p1, __accept_natural_int32(i1), __accept_natural_int32(n));
		return Qnil;
	}
	rb_raise(rb_eTypeError, "expected array");
}
static VALUE
r_Float32Array_each(VALUE self)
{
	Float32Array *p;
	Data_Get_Struct(self, Float32Array, p);
	int32_t i = 0;
	while (i < p->length) {
		rb_yield(__allocate_Float32(p->data[i]));
		i += 1;
	}
	return Qnil;
}
static VALUE
r_Float64Array_clone(VALUE self)
{
	Float64Array *p0;
	Data_Get_Struct(self, Float64Array, p0);
	int32_t n = p0->length;
	Float64Array *p1 = xmalloc(sizeof(Float64Array) + sizeof(double) * (size_t) n);
	if (p1 == NULL)
		rb_raise(rb_eNoMemError, "NoMemoryError");
	p1->length = n;
	int32_t i;
	for (i = 0; i < n; i += 1)
		p1->data[i] = p0->data[i];
	return Data_Wrap_Struct(RBASIC(self)->klass, NULL, NULL, p1);
}
static VALUE
r_Float64Array_length(VALUE self)
{
	Float64Array *p;
	Data_Get_Struct(self, Float64Array, p);
	return __allocate_Int32(p->length);
}
static VALUE
r_Float64Array_get(VALUE self, VALUE index)
{
	Float64Array *p;
	Data_Get_Struct(self, Float64Array, p);
	int32_t i = __accept_bounded_int32(index, p->length);
	return __allocate_Float64(p->data[i]);
}
static VALUE
r_Float64Array_set(VALUE self, VALUE index, VALUE e)
{
	Float64Array *p;
	Data_Get_Struct(self, Float64Array, p);
	int32_t i = __accept_bounded_int32(index, p->length);
	p->data[i] = __accept_float64(e);
	return Qnil;
}
static VALUE
r_Float64Array_copy(VALUE self, VALUE i0, VALUE m1, VALUE i1, VALUE n)
{
	Float64Array *p0;
	Data_Get_Struct(self, Float64Array, p0);
	if (TYPE(m1) == T_DATA && RBASIC(m1)->klass == r_Float64Array) {
		Float64Array *p1;
		Data_Get_Struct(m1, Float64Array, p1);
		__arraycopy_Float64Array(p0, __accept_natural_int32(i0), p1, __accept_natural_int32(i1), __accept_natural_int32(n));
		return Qnil;
	}
	rb_raise(rb_eTypeError, "expected array");
}
static VALUE
r_Float64Array_each(VALUE self)
{
	Float64Array *p;
	Data_Get_Struct(self, Float64Array, p);
	int32_t i = 0;
	while (i < p->length) {
		rb_yield(__allocate_Float64(p->data[i]));
		i += 1;
	}
	return Qnil;
}
static VALUE
r_JAVA_ARRAYCOPY(VALUE self, VALUE m0, VALUE i0, VALUE m1, VALUE i1, VALUE n)
{
	rb_funcall(m0, COPY_ID, 4, i0, m1, i1, n);
	return Qnil;
}
static VALUE
__java_lang_Object()
{
	return rb_funcall(rb_funcall(rb_funcall(r_JAVA, JAVA_ID, 0), LANG_ID, 0), OBJECT_ID, 0);
}
static VALUE
__java_lang_Throwable()
{
	return rb_funcall(rb_funcall(rb_funcall(r_JAVA, JAVA_ID, 0), LANG_ID, 0), THROWABLE_ID, 0);
}
static VALUE
__is_inherited_base(VALUE klass0, VALUE klass1)
{
	if (klass1 == __java_lang_Object())
		return Qtrue;
	if (klass1 == __java_lang_Throwable())
		if (rb_class_inherited_p(klass0, rb_eException) == Qtrue)
			return Qtrue;
	if (rb_class_inherited_p(klass0, klass1) == Qtrue)
		return Qtrue;
	return Qfalse;
}
static VALUE
__is_inherited(VALUE klass0, VALUE klass1)
{
	VALUE inner0, inner1;
	while (1) {
		inner1 = rb_ivar_get(klass1, INNER_ID);
		if (NIL_P(inner1))
			return __is_inherited_base(klass0, klass1);
		__assert_module(inner1);
		klass1 = inner1;
		inner0 = rb_ivar_get(klass0, INNER_ID);
		if (NIL_P(inner0))
			return Qfalse;
		__assert_module(inner0);
		klass0 = inner0;
	}
}
static VALUE
r_ObjectArray_eqv(VALUE self, VALUE o)
{
	return __is_inherited(CLASS_OF(o), self);
}
static VALUE
r_ObjectArray_is$is_a(VALUE self, VALUE module)
{
	if (TYPE(module) == T_MODULE)
		return Qfalse;
	if (TYPE(module) != T_CLASS)
		rb_raise(rb_eTypeError, "expected class or module");
	return __is_inherited(RBASIC(self)->klass, module);
}
static VALUE
r_PrimitiveArray_is$is_a(VALUE self, VALUE module)
{
	if (TYPE(module) == T_MODULE)
		return Qfalse;
	if (TYPE(module) != T_CLASS)
		rb_raise(rb_eTypeError, "expected class or module");
	if (module == CLASS_OF(self))
		return Qtrue;
	if (module == __java_lang_Object())
		return Qtrue;
	return Qfalse;
}
static VALUE
__allocate_Array(VALUE klass, int32_t *counts, int32_t d, int32_t dimensions)
{
	int32_t n = counts[d];
	if (d + 1 == dimensions)
		return rb_funcall(klass, NEW_ID, 1, __allocate_Int32(n));
	VALUE inner = rb_ivar_get(klass, INNER_ID);
	__assert_class(inner);
	VALUE a = __allocate_ObjectArray(klass, n, Qnil);
	ObjectArray *p;
	Data_Get_Struct(a, ObjectArray, p);
	int32_t i;
	for (i = 0; i < n; i += 1)
		p->data[i] = __allocate_Array(inner, counts, d + 1, dimensions);
	return a;
}
struct ArrayFactory {
	VALUE klass;
	int32_t dimensions;
	int32_t counts[];
};
typedef struct ArrayFactory ArrayFactory;
static VALUE r_ArrayFactory;
static VALUE
__allocate_ArrayFactory(VALUE klass, int32_t dimensions)
{
	ArrayFactory *p = xmalloc(sizeof(ArrayFactory) + sizeof(int32_t) * (size_t) dimensions);
	if (p == NULL)
		rb_raise(rb_eNoMemError, "NoMemoryError");
	p->klass = klass;
	p->dimensions = dimensions;
	return Data_Wrap_Struct(r_ArrayFactory, NULL, NULL, p);
}
static VALUE
r_ArrayFactory_new(VALUE self)
{
	ArrayFactory *p;
	Data_Get_Struct(self, ArrayFactory, p);
	VALUE klass = p->klass;
	int32_t d;
	for (d = 1; d < p->dimensions; d += 1)
		klass = __make_outer(klass);
	return __allocate_Array(klass, p->counts, 0, p->dimensions);
}
static VALUE
__new_ArrayFactory(VALUE klass, VALUE *counts, int32_t dimensions)
{
	VALUE f = __allocate_ArrayFactory(klass, dimensions);
	ArrayFactory *p;
	Data_Get_Struct(f, ArrayFactory, p);
	int32_t d;
	for (d = 0; d < dimensions; d += 1)
		p->counts[d] = __accept_natural_int32(counts[d]);
	return f;
}
static VALUE
__splat(VALUE klass, VALUE args)
{
	long len = RARRAY_LEN(args);
	if (len > 0x000000007FFFFFFFL)
		rb_raise(rb_eRangeError, "too many arguments");
	int32_t dimensions = (int32_t) len;
	if (dimensions == 0)
		return klass;
	VALUE *counts = RARRAY_PTR(args);
	return __new_ArrayFactory(klass, counts, dimensions);
}
static VALUE
r_JavaArray_splat(VALUE self, VALUE args)
{
	return __splat(__make_outer(self), args);
}
struct PrimitiveArrayFactoryFactory {
	VALUE klass;
};
typedef struct PrimitiveArrayFactoryFactory PrimitiveArrayFactoryFactory;
static VALUE r_PrimitiveArrayFactoryFactory;
static VALUE
__allocate_PrimitiveArrayFactoryFactory(VALUE klass)
{
	PrimitiveArrayFactoryFactory *p = ALLOC(PrimitiveArrayFactoryFactory);
	if (p == NULL)
		rb_raise(rb_eNoMemError, "NoMemoryError");
	p->klass = klass;
	return Data_Wrap_Struct(r_PrimitiveArrayFactoryFactory, NULL, NULL, p);
}
static VALUE
r_PrimitiveArrayFactoryFactory_splat(VALUE self, VALUE args)
{
	PrimitiveArrayFactoryFactory *p;
	Data_Get_Struct(self, PrimitiveArrayFactoryFactory, p);
	return __splat(p->klass, args);
}
static VALUE r_BOOLEAN_ARRAY_FACTORY_FACTORY;
static VALUE
r_JAVA_boolean(VALUE self)
{
	return r_BOOLEAN_ARRAY_FACTORY_FACTORY;
}
static VALUE r_BYTE_ARRAY_FACTORY_FACTORY;
static VALUE
r_JAVA_byte(VALUE self)
{
	return r_BYTE_ARRAY_FACTORY_FACTORY;
}
static VALUE r_CHAR_ARRAY_FACTORY_FACTORY;
static VALUE
r_JAVA_char(VALUE self)
{
	return r_CHAR_ARRAY_FACTORY_FACTORY;
}
static VALUE r_INT16_ARRAY_FACTORY_FACTORY;
static VALUE
r_JAVA_int16(VALUE self)
{
	return r_INT16_ARRAY_FACTORY_FACTORY;
}
static VALUE r_INT32_ARRAY_FACTORY_FACTORY;
static VALUE
r_JAVA_int32(VALUE self)
{
	return r_INT32_ARRAY_FACTORY_FACTORY;
}
static VALUE r_INT64_ARRAY_FACTORY_FACTORY;
static VALUE
r_JAVA_int64(VALUE self)
{
	return r_INT64_ARRAY_FACTORY_FACTORY;
}
static VALUE r_FLOAT32_ARRAY_FACTORY_FACTORY;
static VALUE
r_JAVA_float32(VALUE self)
{
	return r_FLOAT32_ARRAY_FACTORY_FACTORY;
}
static VALUE r_FLOAT64_ARRAY_FACTORY_FACTORY;
static VALUE
r_JAVA_float64(VALUE self)
{
	return r_FLOAT64_ARRAY_FACTORY_FACTORY;
}
static VALUE __new_KlassArrayFactoryFactory(VALUE);
static VALUE
r_KlassArrayFactoryFactory_splat(VALUE self, VALUE args)
{
	VALUE module = rb_ivar_get(self, MODULE_ID);
	__assert_module(module);
	return __splat(__make_outer(module), args);
}
static VALUE
r_KlassArrayFactoryFactory_const_missing(VALUE self, VALUE symbol)
{
	if (TYPE(symbol) != T_SYMBOL)
		rb_raise(rb_eTypeError, "expected symbol");
	ID id = SYM2ID(symbol);
	VALUE module = rb_ivar_get(self, MODULE_ID);
	__assert_module(module);
	VALUE o = rb_const_get(module, id);
	if (TYPE(o) != T_MODULE && TYPE(o) != T_CLASS)
		rb_raise(rb_eTypeError, "expected class or module");
	return __new_KlassArrayFactoryFactory(o);
}
static VALUE
__new_KlassArrayFactoryFactory(VALUE module)
{
	VALUE wrapper = rb_module_new();
	rb_define_module_function(wrapper, "[]", r_KlassArrayFactoryFactory_splat, -2);
	rb_define_module_function(wrapper, "const_missing", r_KlassArrayFactoryFactory_const_missing, 1);
	rb_ivar_set(wrapper, MODULE_ID, module);
	return wrapper;
}
static VALUE r_RUBY;
static VALUE
r_JAVA_ruby(VALUE self)
{
	return r_RUBY;
}
static VALUE
r_RUBY_method_missing(VALUE self, VALUE symbol)
{
	if (TYPE(symbol) != T_SYMBOL)
		rb_raise(rb_eTypeError, "expected symbol");
	ID id = SYM2ID(symbol);
	VALUE o = rb_const_get(rb_cObject, id);
	if (TYPE(o) != T_MODULE && TYPE(o) != T_CLASS)
		rb_raise(rb_eTypeError, "expected class or module");
	return __new_KlassArrayFactoryFactory(o);
}
static VALUE r_ClassMixin;
static VALUE
r_ClassMixin_splat(VALUE self, VALUE args)
{
	return __splat(__make_outer(self), args);
}
static VALUE
r_Module_set_name(VALUE self, VALUE o)
{
	if (TYPE(o) != T_STRING)
		rb_raise(rb_eTypeError, "expected string");
	char *p = RSTRING_PTR(o);
	long len = RSTRING_LEN(o);
	ID id = rb_intern2(p, len);
	rb_name_class(self, id);
	return Qnil;
}
static VALUE
r_String_from_j_bytes(VALUE self, VALUE a)
{
	if (TYPE(a) == T_DATA && RBASIC(a)->klass == r_ByteArray) {
		ByteArray *p;
		Data_Get_Struct(a, ByteArray, p);
		return rb_str_new((char*) p->data, p->length);
	}
	rb_raise(rb_eTypeError, "expected byte[]");
}
static VALUE
r_String_to_j_bytes(VALUE self)
{
	long len = RSTRING_LEN(self);
	if (len > 0x000000007FFFFFFFL)
		rb_raise(rb_eRangeError, "string too big");
	int32_t n = (int32_t) len;
	char *data = RSTRING_PTR(self);
	ByteArray *p = xmalloc(sizeof(ByteArray) + sizeof(int8_t) * (size_t) n);
	if (p == NULL)
		rb_raise(rb_eNoMemError, "NoMemoryError");
	p->length = n;
	int32_t i;
	for (i = 0; i < n; i += 1)
		p->data[i] = data[i];
	return Data_Wrap_Struct(r_ByteArray, NULL, NULL, p);
}
void
Init_primitive()
{
	r_INTEGER_X8000000000000000 = ULONG2NUM(0x8000000000000000UL);
	rb_global_variable(&r_INTEGER_X8000000000000000);
	r_INTEGER_XFFFFFFFFFFFFFFFF = ULONG2NUM(0xFFFFFFFFFFFFFFFFUL);
	rb_global_variable(&r_INTEGER_XFFFFFFFFFFFFFFFF);
	r_INTEGER_MX8000000000000000 = LONG2NUM(-0x7FFFFFFFFFFFFFFFL - 1);
	rb_global_variable(&r_INTEGER_MX8000000000000000);
	r_JAVA = rb_define_module("JAVA");
	r_JavaValue = rb_define_class_under(r_JAVA, "Value", rb_cBasicObject);
	rb_include_module(r_JavaValue, rb_mKernel);
	rb_define_module_function(r_JavaValue, "const_missing", r_JavaValue_const_missing, 1);
	r_JavaBasicObject = rb_define_class_under(r_JAVA, "BasicObject", r_JavaValue);
	rb_define_module_function(r_JavaBasicObject, "===", r_JavaBasicObject_eqv, 1);
	r_JavaArray = rb_define_class_under(r_JAVA, "Array", r_JavaValue);
	rb_define_method(r_JavaArray, "equals", r_JavaArray_equals, 1);
	rb_define_method(r_JavaArray, "hashCode", r_JavaArray_hashCode, 0);
	rb_define_method(r_JavaArray, "toString", r_JavaArray_toString, 0);
	rb_define_method(r_JavaArray, "eql?", r_JavaArray_is$eql, 1);
	rb_define_method(r_JavaArray, "hash", r_JavaArray_hash, 0);
	rb_define_method(r_JavaArray, "to_s", r_JavaArray_to_s, 0);
	r_JavaException = rb_define_class_under(r_JAVA, "Exception", rb_eException);
	rb_define_module_function(r_JavaException, "new", r_JavaException_new, 1);
	rb_define_method(r_JavaException, "is_a?", r_JavaException_is$is_a, 1);
	rb_define_method(r_JavaException, "cause", r_JavaException_cause, 0);
	rb_define_virtual_variable("$error", errinfo_getter, NULL);
	r_Byte = rb_define_class("Byte", r_JavaValue);
	rb_undef_alloc_func(r_Byte);
	rb_define_module_function(r_Byte, "[]", r_Byte_new, 1);
	rb_define_method(r_Byte, "===", r_Byte_equals, 1);
	rb_define_method(r_Byte, "equals", r_Byte_equals, 1);
	rb_define_method(r_Byte, "hashCode", r_Byte_hashCode, 0);
	rb_define_method(r_Byte, "toString", r_Byte_toString, 0);
	rb_define_method(r_Byte, "eql?", r_Byte_equals, 1);
	rb_define_method(r_Byte, "hash", r_Byte_hash, 0);
	rb_define_method(r_Byte, "to_s", r_Byte_to_s, 0);
	rb_define_method(r_Byte, "inspect", r_Byte_to_s, 0);
	rb_define_method(r_Byte, "to_byte", r_Byte_to_byte, 0);
	rb_define_method(r_Byte, "to_char", r_Byte_to_char, 0);
	rb_define_method(r_Byte, "to_int16", r_Byte_to_int16, 0);
	rb_define_method(r_Byte, "to_int32", r_Byte_to_int32, 0);
	rb_define_method(r_Byte, "to_int64", r_Byte_to_int64, 0);
	rb_define_method(r_Byte, "to_float32", r_Byte_to_float32, 0);
	rb_define_method(r_Byte, "to_float64", r_Byte_to_float64, 0);
	rb_define_method(r_Byte, "to_i", r_Byte_to_i, 0);
	rb_define_method(r_Byte, "to_int", r_Byte_to_i, 0);
	rb_define_method(r_Byte, "to_f", r_Byte_to_f, 0);
	rb_define_method(r_Byte, "==", r_Byte_eq, 1);
	rb_define_method(r_Byte, "!=", r_Byte_ne, 1);
	rb_define_method(r_Byte, "+@", r_Byte_pos, 0);
	rb_define_method(r_Byte, "-@", r_Byte_neg, 0);
	rb_define_method(r_Byte, "*", r_Byte_mul, 1);
	rb_define_method(r_Byte, "/", r_Byte_div, 1);
	rb_define_method(r_Byte, "%", r_Byte_mod, 1);
	rb_define_method(r_Byte, "+", r_Byte_add, 1);
	rb_define_method(r_Byte, "-", r_Byte_sub, 1);
	rb_define_method(r_Byte, "<=>", r_Byte_cmp, 1);
	rb_define_method(r_Byte, "<", r_Byte_lt, 1);
	rb_define_method(r_Byte, "<=", r_Byte_le, 1);
	rb_define_method(r_Byte, ">=", r_Byte_ge, 1);
	rb_define_method(r_Byte, ">", r_Byte_gt, 1);
	rb_define_method(r_Byte, "~", r_Byte_inv, 0);
	rb_define_method(r_Byte, "<<", r_Byte_shl, 1);
	rb_define_method(r_Byte, ">>", r_Byte_shr, 1);
	rb_define_method(r_Byte, "ushr", r_Byte_ushr, 1);
	rb_define_method(r_Byte, "&", r_Byte_and, 1);
	rb_define_method(r_Byte, "^", r_Byte_xor, 1);
	rb_define_method(r_Byte, "|", r_Byte_or, 1);
	rb_define_method(r_Byte, "zero?", r_Byte_is$zero, 0);
	r_Char = rb_define_class("Char", r_JavaValue);
	rb_undef_alloc_func(r_Char);
	rb_define_module_function(r_Char, "[]", r_Char_new, 1);
	rb_define_method(r_Char, "===", r_Char_equals, 1);
	rb_define_method(r_Char, "equals", r_Char_equals, 1);
	rb_define_method(r_Char, "hashCode", r_Char_hashCode, 0);
	rb_define_method(r_Char, "toString", r_Char_toString, 0);
	rb_define_method(r_Char, "eql?", r_Char_equals, 1);
	rb_define_method(r_Char, "hash", r_Char_hash, 0);
	rb_define_method(r_Char, "to_s", r_Char_to_s, 0);
	rb_define_method(r_Char, "inspect", r_Char_to_s, 0);
	rb_define_method(r_Char, "to_byte", r_Char_to_byte, 0);
	rb_define_method(r_Char, "to_char", r_Char_to_char, 0);
	rb_define_method(r_Char, "to_int16", r_Char_to_int16, 0);
	rb_define_method(r_Char, "to_int32", r_Char_to_int32, 0);
	rb_define_method(r_Char, "to_int64", r_Char_to_int64, 0);
	rb_define_method(r_Char, "to_float32", r_Char_to_float32, 0);
	rb_define_method(r_Char, "to_float64", r_Char_to_float64, 0);
	rb_define_method(r_Char, "to_i", r_Char_to_i, 0);
	rb_define_method(r_Char, "to_int", r_Char_to_i, 0);
	rb_define_method(r_Char, "to_f", r_Char_to_f, 0);
	rb_define_method(r_Char, "==", r_Char_eq, 1);
	rb_define_method(r_Char, "!=", r_Char_ne, 1);
	rb_define_method(r_Char, "+@", r_Char_pos, 0);
	rb_define_method(r_Char, "-@", r_Char_neg, 0);
	rb_define_method(r_Char, "*", r_Char_mul, 1);
	rb_define_method(r_Char, "/", r_Char_div, 1);
	rb_define_method(r_Char, "%", r_Char_mod, 1);
	rb_define_method(r_Char, "+", r_Char_add, 1);
	rb_define_method(r_Char, "-", r_Char_sub, 1);
	rb_define_method(r_Char, "<=>", r_Char_cmp, 1);
	rb_define_method(r_Char, "<", r_Char_lt, 1);
	rb_define_method(r_Char, "<=", r_Char_le, 1);
	rb_define_method(r_Char, ">=", r_Char_ge, 1);
	rb_define_method(r_Char, ">", r_Char_gt, 1);
	rb_define_method(r_Char, "~", r_Char_inv, 0);
	rb_define_method(r_Char, "<<", r_Char_shl, 1);
	rb_define_method(r_Char, ">>", r_Char_shr, 1);
	rb_define_method(r_Char, "ushr", r_Char_ushr, 1);
	rb_define_method(r_Char, "&", r_Char_and, 1);
	rb_define_method(r_Char, "^", r_Char_xor, 1);
	rb_define_method(r_Char, "|", r_Char_or, 1);
	rb_define_method(r_Char, "zero?", r_Char_is$zero, 0);
	r_Int16 = rb_define_class("Int16", r_JavaValue);
	rb_undef_alloc_func(r_Int16);
	rb_define_module_function(r_Int16, "[]", r_Int16_new, 1);
	rb_define_method(r_Int16, "===", r_Int16_equals, 1);
	rb_define_method(r_Int16, "equals", r_Int16_equals, 1);
	rb_define_method(r_Int16, "hashCode", r_Int16_hashCode, 0);
	rb_define_method(r_Int16, "toString", r_Int16_toString, 0);
	rb_define_method(r_Int16, "eql?", r_Int16_equals, 1);
	rb_define_method(r_Int16, "hash", r_Int16_hash, 0);
	rb_define_method(r_Int16, "to_s", r_Int16_to_s, 0);
	rb_define_method(r_Int16, "inspect", r_Int16_to_s, 0);
	rb_define_method(r_Int16, "to_byte", r_Int16_to_byte, 0);
	rb_define_method(r_Int16, "to_char", r_Int16_to_char, 0);
	rb_define_method(r_Int16, "to_int16", r_Int16_to_int16, 0);
	rb_define_method(r_Int16, "to_int32", r_Int16_to_int32, 0);
	rb_define_method(r_Int16, "to_int64", r_Int16_to_int64, 0);
	rb_define_method(r_Int16, "to_float32", r_Int16_to_float32, 0);
	rb_define_method(r_Int16, "to_float64", r_Int16_to_float64, 0);
	rb_define_method(r_Int16, "to_i", r_Int16_to_i, 0);
	rb_define_method(r_Int16, "to_int", r_Int16_to_i, 0);
	rb_define_method(r_Int16, "to_f", r_Int16_to_f, 0);
	rb_define_method(r_Int16, "==", r_Int16_eq, 1);
	rb_define_method(r_Int16, "!=", r_Int16_ne, 1);
	rb_define_method(r_Int16, "+@", r_Int16_pos, 0);
	rb_define_method(r_Int16, "-@", r_Int16_neg, 0);
	rb_define_method(r_Int16, "*", r_Int16_mul, 1);
	rb_define_method(r_Int16, "/", r_Int16_div, 1);
	rb_define_method(r_Int16, "%", r_Int16_mod, 1);
	rb_define_method(r_Int16, "+", r_Int16_add, 1);
	rb_define_method(r_Int16, "-", r_Int16_sub, 1);
	rb_define_method(r_Int16, "<=>", r_Int16_cmp, 1);
	rb_define_method(r_Int16, "<", r_Int16_lt, 1);
	rb_define_method(r_Int16, "<=", r_Int16_le, 1);
	rb_define_method(r_Int16, ">=", r_Int16_ge, 1);
	rb_define_method(r_Int16, ">", r_Int16_gt, 1);
	rb_define_method(r_Int16, "~", r_Int16_inv, 0);
	rb_define_method(r_Int16, "<<", r_Int16_shl, 1);
	rb_define_method(r_Int16, ">>", r_Int16_shr, 1);
	rb_define_method(r_Int16, "ushr", r_Int16_ushr, 1);
	rb_define_method(r_Int16, "&", r_Int16_and, 1);
	rb_define_method(r_Int16, "^", r_Int16_xor, 1);
	rb_define_method(r_Int16, "|", r_Int16_or, 1);
	rb_define_method(r_Int16, "zero?", r_Int16_is$zero, 0);
	r_Int32 = rb_define_class("Int32", r_JavaValue);
	rb_undef_alloc_func(r_Int32);
	rb_define_module_function(r_Int32, "[]", r_Int32_new, 1);
	rb_define_method(r_Int32, "===", r_Int32_equals, 1);
	rb_define_method(r_Int32, "equals", r_Int32_equals, 1);
	rb_define_method(r_Int32, "hashCode", r_Int32_hashCode, 0);
	rb_define_method(r_Int32, "toString", r_Int32_toString, 0);
	rb_define_method(r_Int32, "eql?", r_Int32_equals, 1);
	rb_define_method(r_Int32, "hash", r_Int32_hash, 0);
	rb_define_method(r_Int32, "to_s", r_Int32_to_s, 0);
	rb_define_method(r_Int32, "inspect", r_Int32_to_s, 0);
	rb_define_method(r_Int32, "as_ruby", r_Int32_to_i, 0);
	rb_define_method(r_Int32, "as_i", r_Int32_to_i, 0);
	rb_define_method(r_Int32, "to_byte", r_Int32_to_byte, 0);
	rb_define_method(r_Int32, "to_char", r_Int32_to_char, 0);
	rb_define_method(r_Int32, "to_int16", r_Int32_to_int16, 0);
	rb_define_method(r_Int32, "to_int32", r_Int32_to_int32, 0);
	rb_define_method(r_Int32, "to_int64", r_Int32_to_int64, 0);
	rb_define_method(r_Int32, "to_float32", r_Int32_to_float32, 0);
	rb_define_method(r_Int32, "to_float64", r_Int32_to_float64, 0);
	rb_define_method(r_Int32, "to_i", r_Int32_to_i, 0);
	rb_define_method(r_Int32, "to_int", r_Int32_to_i, 0);
	rb_define_method(r_Int32, "to_f", r_Int32_to_f, 0);
	rb_define_method(r_Int32, "==", r_Int32_eq, 1);
	rb_define_method(r_Int32, "!=", r_Int32_ne, 1);
	rb_define_method(r_Int32, "+@", r_Int32_pos, 0);
	rb_define_method(r_Int32, "-@", r_Int32_neg, 0);
	rb_define_method(r_Int32, "*", r_Int32_mul, 1);
	rb_define_method(r_Int32, "/", r_Int32_div, 1);
	rb_define_method(r_Int32, "%", r_Int32_mod, 1);
	rb_define_method(r_Int32, "+", r_Int32_add, 1);
	rb_define_method(r_Int32, "-", r_Int32_sub, 1);
	rb_define_method(r_Int32, "<=>", r_Int32_cmp, 1);
	rb_define_method(r_Int32, "<", r_Int32_lt, 1);
	rb_define_method(r_Int32, "<=", r_Int32_le, 1);
	rb_define_method(r_Int32, ">=", r_Int32_ge, 1);
	rb_define_method(r_Int32, ">", r_Int32_gt, 1);
	rb_define_method(r_Int32, "~", r_Int32_inv, 0);
	rb_define_method(r_Int32, "<<", r_Int32_shl, 1);
	rb_define_method(r_Int32, ">>", r_Int32_shr, 1);
	rb_define_method(r_Int32, "ushr", r_Int32_ushr, 1);
	rb_define_method(r_Int32, "&", r_Int32_and, 1);
	rb_define_method(r_Int32, "^", r_Int32_xor, 1);
	rb_define_method(r_Int32, "|", r_Int32_or, 1);
	rb_define_method(r_Int32, "zero?", r_Int32_is$zero, 0);
	rb_define_method(r_Int32, "to_int32!", r_Int32_bang$to_int32, 0);
	rb_define_method(r_Int32, "to_int64!", r_Int32_bang$to_int64, 0);
	rb_define_method(r_Int32, "to_fixnum", r_Int32_to_fixnum, 0);
	rb_define_method(r_Int32, "new_fixnum", r_Int32_to_fixnum, 0);
	rb_define_method(r_Int32, "to_hex", r_Int32_to_hex, 0);
	rb_define_method(r_Int32, "chr", r_Int32_chr, 0);
	rb_define_method(r_Int32, "succ", r_Int32_succ, 0);
	rb_define_method(r_Int32, "rol", r_Int32_rol, 1);
	rb_define_method(r_Int32, "ror", r_Int32_ror, 1);
	rb_define_method(r_Int32, "count", r_Int32_count, 0);
	rb_define_method(r_Int32, "signum", r_Int32_signum, 0);
	rb_define_method(r_Int32, "times", r_Int32_times, 0);
	rb_define_method(r_Int32, "upto", r_Int32_upto, 1);
	rb_define_method(r_Int32, "downto", r_Int32_downto, 1);
	r_Int64 = rb_define_class("Int64", r_JavaValue);
	rb_undef_alloc_func(r_Int64);
	rb_define_module_function(r_Int64, "[]", r_Int64_new, 1);
	rb_define_method(r_Int64, "===", r_Int64_equals, 1);
	rb_define_method(r_Int64, "equals", r_Int64_equals, 1);
	rb_define_method(r_Int64, "hashCode", r_Int64_hashCode, 0);
	rb_define_method(r_Int64, "toString", r_Int64_toString, 0);
	rb_define_method(r_Int64, "eql?", r_Int64_equals, 1);
	rb_define_method(r_Int64, "hash", r_Int64_hash, 0);
	rb_define_method(r_Int64, "to_s", r_Int64_to_s, 0);
	rb_define_method(r_Int64, "inspect", r_Int64_to_s, 0);
	rb_define_method(r_Int64, "as_ruby", r_Int64_to_i, 0);
	rb_define_method(r_Int64, "as_i", r_Int64_to_i, 0);
	rb_define_method(r_Int64, "to_byte", r_Int64_to_byte, 0);
	rb_define_method(r_Int64, "to_char", r_Int64_to_char, 0);
	rb_define_method(r_Int64, "to_int16", r_Int64_to_int16, 0);
	rb_define_method(r_Int64, "to_int32", r_Int64_to_int32, 0);
	rb_define_method(r_Int64, "to_int64", r_Int64_to_int64, 0);
	rb_define_method(r_Int64, "to_float32", r_Int64_to_float32, 0);
	rb_define_method(r_Int64, "to_float64", r_Int64_to_float64, 0);
	rb_define_method(r_Int64, "to_i", r_Int64_to_i, 0);
	rb_define_method(r_Int64, "to_int", r_Int64_to_i, 0);
	rb_define_method(r_Int64, "to_f", r_Int64_to_f, 0);
	rb_define_method(r_Int64, "==", r_Int64_eq, 1);
	rb_define_method(r_Int64, "!=", r_Int64_ne, 1);
	rb_define_method(r_Int64, "+@", r_Int64_pos, 0);
	rb_define_method(r_Int64, "-@", r_Int64_neg, 0);
	rb_define_method(r_Int64, "*", r_Int64_mul, 1);
	rb_define_method(r_Int64, "/", r_Int64_div, 1);
	rb_define_method(r_Int64, "%", r_Int64_mod, 1);
	rb_define_method(r_Int64, "+", r_Int64_add, 1);
	rb_define_method(r_Int64, "-", r_Int64_sub, 1);
	rb_define_method(r_Int64, "<=>", r_Int64_cmp, 1);
	rb_define_method(r_Int64, "<", r_Int64_lt, 1);
	rb_define_method(r_Int64, "<=", r_Int64_le, 1);
	rb_define_method(r_Int64, ">=", r_Int64_ge, 1);
	rb_define_method(r_Int64, ">", r_Int64_gt, 1);
	rb_define_method(r_Int64, "~", r_Int64_inv, 0);
	rb_define_method(r_Int64, "<<", r_Int64_shl, 1);
	rb_define_method(r_Int64, ">>", r_Int64_shr, 1);
	rb_define_method(r_Int64, "ushr", r_Int64_ushr, 1);
	rb_define_method(r_Int64, "&", r_Int64_and, 1);
	rb_define_method(r_Int64, "^", r_Int64_xor, 1);
	rb_define_method(r_Int64, "|", r_Int64_or, 1);
	rb_define_method(r_Int64, "zero?", r_Int64_is$zero, 0);
	rb_define_method(r_Int64, "to_int32!", r_Int64_bang$to_int32, 0);
	rb_define_method(r_Int64, "to_int64!", r_Int64_bang$to_int64, 0);
	rb_define_method(r_Int64, "to_fixnum", r_Int64_to_fixnum, 0);
	rb_define_method(r_Int64, "new_fixnum", r_Int64_to_fixnum, 0);
	rb_define_method(r_Int64, "to_hex", r_Int64_to_hex, 0);
	rb_define_method(r_Int64, "succ", r_Int64_succ, 0);
	rb_define_method(r_Int64, "rol", r_Int64_rol, 1);
	rb_define_method(r_Int64, "ror", r_Int64_ror, 1);
	rb_define_method(r_Int64, "count", r_Int64_count, 0);
	rb_define_method(r_Int64, "signum", r_Int64_signum, 0);
	rb_define_method(r_Int64, "times", r_Int64_times, 0);
	r_Float32 = rb_define_class("Float32", r_JavaValue);
	rb_undef_alloc_func(r_Float32);
	rb_define_module_function(r_Float32, "[]", r_Float32_new, 1);
	rb_define_method(r_Float32, "===", r_Float32_equals, 1);
	rb_define_method(r_Float32, "equals", r_Float32_equals, 1);
	rb_define_method(r_Float32, "hashCode", r_Float32_hashCode, 0);
	rb_define_method(r_Float32, "toString", r_Float32_toString, 0);
	rb_define_method(r_Float32, "eql?", r_Float32_equals, 1);
	rb_define_method(r_Float32, "hash", r_Float32_hash, 0);
	rb_define_method(r_Float32, "to_s", r_Float32_to_s, 0);
	rb_define_method(r_Float32, "inspect", r_Float32_to_s, 0);
	rb_define_method(r_Float32, "as_ruby", r_Float32_to_f, 0);
	rb_define_method(r_Float32, "as_f", r_Float32_to_f, 0);
	rb_define_method(r_Float32, "to_byte", r_Float32_to_byte, 0);
	rb_define_method(r_Float32, "to_char", r_Float32_to_char, 0);
	rb_define_method(r_Float32, "to_int16", r_Float32_to_int16, 0);
	rb_define_method(r_Float32, "to_int32", r_Float32_to_int32, 0);
	rb_define_method(r_Float32, "to_int64", r_Float32_to_int64, 0);
	rb_define_method(r_Float32, "to_float32", r_Float32_to_float32, 0);
	rb_define_method(r_Float32, "to_float64", r_Float32_to_float64, 0);
	rb_define_method(r_Float32, "to_i", r_Float32_to_i, 0);
	rb_define_method(r_Float32, "to_int", r_Float32_to_i, 0);
	rb_define_method(r_Float32, "to_f", r_Float32_to_f, 0);
	rb_define_method(r_Float32, "==", r_Float32_eq, 1);
	rb_define_method(r_Float32, "!=", r_Float32_ne, 1);
	rb_define_method(r_Float32, "+@", r_Float32_pos, 0);
	rb_define_method(r_Float32, "-@", r_Float32_neg, 0);
	rb_define_method(r_Float32, "*", r_Float32_mul, 1);
	rb_define_method(r_Float32, "/", r_Float32_div, 1);
	rb_define_method(r_Float32, "%", r_Float32_mod, 1);
	rb_define_method(r_Float32, "+", r_Float32_add, 1);
	rb_define_method(r_Float32, "-", r_Float32_sub, 1);
	rb_define_method(r_Float32, "<=>", r_Float32_cmp, 1);
	rb_define_method(r_Float32, "<", r_Float32_lt, 1);
	rb_define_method(r_Float32, "<=", r_Float32_le, 1);
	rb_define_method(r_Float32, ">=", r_Float32_ge, 1);
	rb_define_method(r_Float32, ">", r_Float32_gt, 1);
	rb_define_method(r_Float32, "zero?", r_Float32_is$zero, 0);
	rb_define_method(r_Float32, "nan?", r_Float32_is$nan, 0);
	rb_define_method(r_Float32, "infinite?", r_Float32_is$infinite, 0);
	rb_define_method(r_Float32, "finite?", r_Float32_is$finite, 0);
	rb_define_const(r_Float32, "NEGATIVE_INFINITY", __allocate_Float32(-INFINITY));
	rb_define_const(r_Float32, "POSITIVE_INFINITY", __allocate_Float32(INFINITY));
	r_Float64 = rb_define_class("Float64", r_JavaValue);
	rb_undef_alloc_func(r_Float64);
	rb_define_module_function(r_Float64, "[]", r_Float64_new, 1);
	rb_define_method(r_Float64, "===", r_Float64_equals, 1);
	rb_define_method(r_Float64, "equals", r_Float64_equals, 1);
	rb_define_method(r_Float64, "hashCode", r_Float64_hashCode, 0);
	rb_define_method(r_Float64, "toString", r_Float64_toString, 0);
	rb_define_method(r_Float64, "eql?", r_Float64_equals, 1);
	rb_define_method(r_Float64, "hash", r_Float64_hash, 0);
	rb_define_method(r_Float64, "to_s", r_Float64_to_s, 0);
	rb_define_method(r_Float64, "inspect", r_Float64_to_s, 0);
	rb_define_method(r_Float64, "as_ruby", r_Float64_to_f, 0);
	rb_define_method(r_Float64, "as_f", r_Float64_to_f, 0);
	rb_define_method(r_Float64, "to_byte", r_Float64_to_byte, 0);
	rb_define_method(r_Float64, "to_char", r_Float64_to_char, 0);
	rb_define_method(r_Float64, "to_int16", r_Float64_to_int16, 0);
	rb_define_method(r_Float64, "to_int32", r_Float64_to_int32, 0);
	rb_define_method(r_Float64, "to_int64", r_Float64_to_int64, 0);
	rb_define_method(r_Float64, "to_float32", r_Float64_to_float32, 0);
	rb_define_method(r_Float64, "to_float64", r_Float64_to_float64, 0);
	rb_define_method(r_Float64, "to_i", r_Float64_to_i, 0);
	rb_define_method(r_Float64, "to_int", r_Float64_to_i, 0);
	rb_define_method(r_Float64, "to_f", r_Float64_to_f, 0);
	rb_define_method(r_Float64, "==", r_Float64_eq, 1);
	rb_define_method(r_Float64, "!=", r_Float64_ne, 1);
	rb_define_method(r_Float64, "+@", r_Float64_pos, 0);
	rb_define_method(r_Float64, "-@", r_Float64_neg, 0);
	rb_define_method(r_Float64, "*", r_Float64_mul, 1);
	rb_define_method(r_Float64, "/", r_Float64_div, 1);
	rb_define_method(r_Float64, "%", r_Float64_mod, 1);
	rb_define_method(r_Float64, "+", r_Float64_add, 1);
	rb_define_method(r_Float64, "-", r_Float64_sub, 1);
	rb_define_method(r_Float64, "<=>", r_Float64_cmp, 1);
	rb_define_method(r_Float64, "<", r_Float64_lt, 1);
	rb_define_method(r_Float64, "<=", r_Float64_le, 1);
	rb_define_method(r_Float64, ">=", r_Float64_ge, 1);
	rb_define_method(r_Float64, ">", r_Float64_gt, 1);
	rb_define_method(r_Float64, "zero?", r_Float64_is$zero, 0);
	rb_define_method(r_Float64, "nan?", r_Float64_is$nan, 0);
	rb_define_method(r_Float64, "infinite?", r_Float64_is$infinite, 0);
	rb_define_method(r_Float64, "finite?", r_Float64_is$finite, 0);
	rb_define_const(r_Float64, "NEGATIVE_INFINITY", __allocate_Float64(-INFINITY));
	rb_define_const(r_Float64, "POSITIVE_INFINITY", __allocate_Float64(INFINITY));
	r_INT32_0 = __allocate_Int32(0);
	rb_global_variable(&r_INT32_0);
	r_INT32_1 = __allocate_Int32(1);
	rb_global_variable(&r_INT32_1);
	r_INT32_M1 = __allocate_Int32(-1);
	rb_global_variable(&r_INT32_M1);
	NEW_ID = rb_intern("new");
	COPY_ID = rb_intern("copy");
	INNER_ID = rb_intern("__ir_inner");
	OUTER_ID = rb_intern("__ir_outer");
	MODULE_ID = rb_intern("__ir_module");
	TO_J_ID = rb_intern("to_j");
	JAVA_ID = rb_intern("java");
	LANG_ID = rb_intern("lang");
	OBJECT_ID = rb_intern("Object");
	THROWABLE_ID = rb_intern("Throwable");
	r_ObjectArray = rb_class_boot(r_JavaArray);
	rb_undef_alloc_func(r_ObjectArray);
	rb_global_variable(&r_ObjectArray);
	rb_define_module_function(r_ObjectArray, "new", r_ObjectArray_new, -1);
	rb_define_module_function(r_ObjectArray, "of", r_ObjectArray_of, -2);
	rb_define_module_function(r_ObjectArray, "[]", r_JavaArray_splat, -2);
	rb_define_module_function(r_ObjectArray, "===", r_ObjectArray_eqv, 1);
	rb_define_method(r_ObjectArray, "is_a?", r_ObjectArray_is$is_a, 1);
	rb_define_method(r_ObjectArray, "clone", r_ObjectArray_clone, 0);
	rb_define_method(r_ObjectArray, "dup", r_ObjectArray_clone, 0);
	rb_define_method(r_ObjectArray, "length", r_ObjectArray_length, 0);
	rb_define_method(r_ObjectArray, "[]", r_ObjectArray_get, 1);
	rb_define_method(r_ObjectArray, "[]=", r_ObjectArray_set, 2);
	rb_define_method(r_ObjectArray, "copy", r_ObjectArray_copy, 4);
	rb_define_method(r_ObjectArray, "each", r_ObjectArray_each, 0);
	r_BooleanArray = rb_class_boot(r_JavaArray);
	rb_undef_alloc_func(r_BooleanArray);
	rb_global_variable(&r_BooleanArray);
	rb_name_class(r_BooleanArray, rb_intern("boolean[]"));
	rb_define_module_function(r_BooleanArray, "new", r_BooleanArray_new, -1);
	rb_define_module_function(r_BooleanArray, "of", r_BooleanArray_of, -2);
	rb_define_module_function(r_BooleanArray, "[]", r_JavaArray_splat, -2);
	rb_define_method(r_BooleanArray, "is_a?", r_PrimitiveArray_is$is_a, 1);
	rb_define_method(r_BooleanArray, "clone", r_BooleanArray_clone, 0);
	rb_define_method(r_BooleanArray, "dup", r_BooleanArray_clone, 0);
	rb_define_method(r_BooleanArray, "length", r_BooleanArray_length, 0);
	rb_define_method(r_BooleanArray, "[]", r_BooleanArray_get, 1);
	rb_define_method(r_BooleanArray, "[]=", r_BooleanArray_set, 2);
	rb_define_method(r_BooleanArray, "copy", r_BooleanArray_copy, 4);
	rb_define_method(r_BooleanArray, "each", r_BooleanArray_each, 0);
	r_ByteArray = rb_class_boot(r_JavaArray);
	rb_undef_alloc_func(r_ByteArray);
	rb_global_variable(&r_ByteArray);
	rb_name_class(r_ByteArray, rb_intern("byte[]"));
	rb_define_module_function(r_ByteArray, "new", r_ByteArray_new, -1);
	rb_define_module_function(r_ByteArray, "of", r_ByteArray_of, -2);
	rb_define_module_function(r_ByteArray, "[]", r_JavaArray_splat, -2);
	rb_define_method(r_ByteArray, "is_a?", r_PrimitiveArray_is$is_a, 1);
	rb_define_method(r_ByteArray, "to_s", r_ByteArray_to_s, 0);
	rb_define_method(r_ByteArray, "clone", r_ByteArray_clone, 0);
	rb_define_method(r_ByteArray, "dup", r_ByteArray_clone, 0);
	rb_define_method(r_ByteArray, "length", r_ByteArray_length, 0);
	rb_define_method(r_ByteArray, "[]", r_ByteArray_get, 1);
	rb_define_method(r_ByteArray, "[]=", r_ByteArray_set, 2);
	rb_define_method(r_ByteArray, "copy", r_ByteArray_copy, 4);
	rb_define_method(r_ByteArray, "each", r_ByteArray_each, 0);
	r_CharArray = rb_class_boot(r_JavaArray);
	rb_undef_alloc_func(r_CharArray);
	rb_global_variable(&r_CharArray);
	rb_name_class(r_CharArray, rb_intern("char[]"));
	rb_define_module_function(r_CharArray, "new", r_CharArray_new, -1);
	rb_define_module_function(r_CharArray, "of", r_CharArray_of, -2);
	rb_define_module_function(r_CharArray, "[]", r_JavaArray_splat, -2);
	rb_define_method(r_CharArray, "is_a?", r_PrimitiveArray_is$is_a, 1);
	rb_define_method(r_CharArray, "clone", r_CharArray_clone, 0);
	rb_define_method(r_CharArray, "dup", r_CharArray_clone, 0);
	rb_define_method(r_CharArray, "length", r_CharArray_length, 0);
	rb_define_method(r_CharArray, "[]", r_CharArray_get, 1);
	rb_define_method(r_CharArray, "[]=", r_CharArray_set, 2);
	rb_define_method(r_CharArray, "copy", r_CharArray_copy, 4);
	rb_define_method(r_CharArray, "each", r_CharArray_each, 0);
	r_Int16Array = rb_class_boot(r_JavaArray);
	rb_undef_alloc_func(r_Int16Array);
	rb_global_variable(&r_Int16Array);
	rb_name_class(r_Int16Array, rb_intern("int16[]"));
	rb_define_module_function(r_Int16Array, "new", r_Int16Array_new, -1);
	rb_define_module_function(r_Int16Array, "of", r_Int16Array_of, -2);
	rb_define_module_function(r_Int16Array, "[]", r_JavaArray_splat, -2);
	rb_define_method(r_Int16Array, "is_a?", r_PrimitiveArray_is$is_a, 1);
	rb_define_method(r_Int16Array, "clone", r_Int16Array_clone, 0);
	rb_define_method(r_Int16Array, "dup", r_Int16Array_clone, 0);
	rb_define_method(r_Int16Array, "length", r_Int16Array_length, 0);
	rb_define_method(r_Int16Array, "[]", r_Int16Array_get, 1);
	rb_define_method(r_Int16Array, "[]=", r_Int16Array_set, 2);
	rb_define_method(r_Int16Array, "copy", r_Int16Array_copy, 4);
	rb_define_method(r_Int16Array, "each", r_Int16Array_each, 0);
	r_Int32Array = rb_class_boot(r_JavaArray);
	rb_undef_alloc_func(r_Int32Array);
	rb_global_variable(&r_Int32Array);
	rb_name_class(r_Int32Array, rb_intern("int32[]"));
	rb_define_module_function(r_Int32Array, "new", r_Int32Array_new, -1);
	rb_define_module_function(r_Int32Array, "of", r_Int32Array_of, -2);
	rb_define_module_function(r_Int32Array, "[]", r_JavaArray_splat, -2);
	rb_define_method(r_Int32Array, "is_a?", r_PrimitiveArray_is$is_a, 1);
	rb_define_method(r_Int32Array, "clone", r_Int32Array_clone, 0);
	rb_define_method(r_Int32Array, "dup", r_Int32Array_clone, 0);
	rb_define_method(r_Int32Array, "length", r_Int32Array_length, 0);
	rb_define_method(r_Int32Array, "[]", r_Int32Array_get, 1);
	rb_define_method(r_Int32Array, "[]=", r_Int32Array_set, 2);
	rb_define_method(r_Int32Array, "copy", r_Int32Array_copy, 4);
	rb_define_method(r_Int32Array, "each", r_Int32Array_each, 0);
	r_Int64Array = rb_class_boot(r_JavaArray);
	rb_undef_alloc_func(r_Int64Array);
	rb_global_variable(&r_Int64Array);
	rb_name_class(r_Int64Array, rb_intern("int64[]"));
	rb_define_module_function(r_Int64Array, "new", r_Int64Array_new, -1);
	rb_define_module_function(r_Int64Array, "of", r_Int64Array_of, -2);
	rb_define_module_function(r_Int64Array, "[]", r_JavaArray_splat, -2);
	rb_define_method(r_Int64Array, "is_a?", r_PrimitiveArray_is$is_a, 1);
	rb_define_method(r_Int64Array, "clone", r_Int64Array_clone, 0);
	rb_define_method(r_Int64Array, "dup", r_Int64Array_clone, 0);
	rb_define_method(r_Int64Array, "length", r_Int64Array_length, 0);
	rb_define_method(r_Int64Array, "[]", r_Int64Array_get, 1);
	rb_define_method(r_Int64Array, "[]=", r_Int64Array_set, 2);
	rb_define_method(r_Int64Array, "copy", r_Int64Array_copy, 4);
	rb_define_method(r_Int64Array, "each", r_Int64Array_each, 0);
	r_Float32Array = rb_class_boot(r_JavaArray);
	rb_undef_alloc_func(r_Float32Array);
	rb_global_variable(&r_Float32Array);
	rb_name_class(r_Float32Array, rb_intern("float32[]"));
	rb_define_module_function(r_Float32Array, "new", r_Float32Array_new, -1);
	rb_define_module_function(r_Float32Array, "of", r_Float32Array_of, -2);
	rb_define_module_function(r_Float32Array, "[]", r_JavaArray_splat, -2);
	rb_define_method(r_Float32Array, "is_a?", r_PrimitiveArray_is$is_a, 1);
	rb_define_method(r_Float32Array, "clone", r_Float32Array_clone, 0);
	rb_define_method(r_Float32Array, "dup", r_Float32Array_clone, 0);
	rb_define_method(r_Float32Array, "length", r_Float32Array_length, 0);
	rb_define_method(r_Float32Array, "[]", r_Float32Array_get, 1);
	rb_define_method(r_Float32Array, "[]=", r_Float32Array_set, 2);
	rb_define_method(r_Float32Array, "copy", r_Float32Array_copy, 4);
	rb_define_method(r_Float32Array, "each", r_Float32Array_each, 0);
	r_Float64Array = rb_class_boot(r_JavaArray);
	rb_undef_alloc_func(r_Float64Array);
	rb_global_variable(&r_Float64Array);
	rb_name_class(r_Float64Array, rb_intern("float64[]"));
	rb_define_module_function(r_Float64Array, "new", r_Float64Array_new, -1);
	rb_define_module_function(r_Float64Array, "of", r_Float64Array_of, -2);
	rb_define_module_function(r_Float64Array, "[]", r_JavaArray_splat, -2);
	rb_define_method(r_Float64Array, "is_a?", r_PrimitiveArray_is$is_a, 1);
	rb_define_method(r_Float64Array, "clone", r_Float64Array_clone, 0);
	rb_define_method(r_Float64Array, "dup", r_Float64Array_clone, 0);
	rb_define_method(r_Float64Array, "length", r_Float64Array_length, 0);
	rb_define_method(r_Float64Array, "[]", r_Float64Array_get, 1);
	rb_define_method(r_Float64Array, "[]=", r_Float64Array_set, 2);
	rb_define_method(r_Float64Array, "copy", r_Float64Array_copy, 4);
	rb_define_method(r_Float64Array, "each", r_Float64Array_each, 0);
	r_ArrayFactory = rb_class_boot(rb_cObject);
	rb_undef_alloc_func(r_ArrayFactory);
	rb_global_variable(&r_ArrayFactory);
	rb_define_method(r_ArrayFactory, "new", r_ArrayFactory_new, 0);
	r_PrimitiveArrayFactoryFactory = rb_class_boot(rb_cObject);
	rb_undef_alloc_func(r_PrimitiveArrayFactoryFactory);
	rb_global_variable(&r_PrimitiveArrayFactoryFactory);
	rb_define_method(r_PrimitiveArrayFactoryFactory, "[]", r_PrimitiveArrayFactoryFactory_splat, -2);
	r_BOOLEAN_ARRAY_FACTORY_FACTORY = __allocate_PrimitiveArrayFactoryFactory(r_BooleanArray);
	rb_global_variable(&r_BOOLEAN_ARRAY_FACTORY_FACTORY);
	r_BYTE_ARRAY_FACTORY_FACTORY = __allocate_PrimitiveArrayFactoryFactory(r_ByteArray);
	rb_global_variable(&r_BYTE_ARRAY_FACTORY_FACTORY);
	r_CHAR_ARRAY_FACTORY_FACTORY = __allocate_PrimitiveArrayFactoryFactory(r_CharArray);
	rb_global_variable(&r_CHAR_ARRAY_FACTORY_FACTORY);
	r_INT16_ARRAY_FACTORY_FACTORY = __allocate_PrimitiveArrayFactoryFactory(r_Int16Array);
	rb_global_variable(&r_INT16_ARRAY_FACTORY_FACTORY);
	r_INT32_ARRAY_FACTORY_FACTORY = __allocate_PrimitiveArrayFactoryFactory(r_Int32Array);
	rb_global_variable(&r_INT32_ARRAY_FACTORY_FACTORY);
	r_INT64_ARRAY_FACTORY_FACTORY = __allocate_PrimitiveArrayFactoryFactory(r_Int64Array);
	rb_global_variable(&r_INT64_ARRAY_FACTORY_FACTORY);
	r_FLOAT32_ARRAY_FACTORY_FACTORY = __allocate_PrimitiveArrayFactoryFactory(r_Float32Array);
	rb_global_variable(&r_FLOAT32_ARRAY_FACTORY_FACTORY);
	r_FLOAT64_ARRAY_FACTORY_FACTORY = __allocate_PrimitiveArrayFactoryFactory(r_Float64Array);
	rb_global_variable(&r_FLOAT64_ARRAY_FACTORY_FACTORY);
	r_RUBY = rb_module_new();
	rb_global_variable(&r_RUBY);
	rb_define_module_function(r_JAVA, "ruby", r_JAVA_ruby, 0);
	rb_define_module_function(r_RUBY, "method_missing", r_RUBY_method_missing, 1);
	r_ClassMixin = rb_define_module_under(r_JAVA, "ClassMixin");
	rb_define_method(r_ClassMixin, "[]", r_ClassMixin_splat, -2);
	rb_define_module_function(r_JAVA, "ARRAYCOPY", r_JAVA_ARRAYCOPY, 5);
	rb_define_module_function(r_JAVA, "boolean", r_JAVA_boolean, 0);
	rb_define_module_function(r_JAVA, "byte", r_JAVA_byte, 0);
	rb_define_module_function(r_JAVA, "char", r_JAVA_char, 0);
	rb_define_module_function(r_JAVA, "int16", r_JAVA_int16, 0);
	rb_define_module_function(r_JAVA, "int32", r_JAVA_int32, 0);
	rb_define_module_function(r_JAVA, "int64", r_JAVA_int64, 0);
	rb_define_module_function(r_JAVA, "float32", r_JAVA_float32, 0);
	rb_define_module_function(r_JAVA, "float64", r_JAVA_float64, 0);
	rb_define_method(rb_cModule, "name=", r_Module_set_name, 1);
	rb_define_module_function(rb_cString, "from_j_bytes", r_String_from_j_bytes, 1);
	rb_define_method(rb_cString, "to_j_bytes", r_String_to_j_bytes, 0);
	return;
}
