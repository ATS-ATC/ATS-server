(function (global, factory) {
  typeof exports === 'object' && typeof module !== 'undefined' ? factory(require('jquery')) :
  typeof define === 'function' && define.amd ? define(['jquery'], factory) :
  (global = global || self, factory(global.jQuery));
}(this, function ($) { 'use strict';

  $ = $ && $.hasOwnProperty('default') ? $['default'] : $;

  var aFunction = function (it) {
    if (typeof it != 'function') {
      throw TypeError(String(it) + ' is not a function');
    } return it;
  };

  // optional / simple context binding
  var bindContext = function (fn, that, length) {
    aFunction(fn);
    if (that === undefined) return fn;
    switch (length) {
      case 0: return function () {
        return fn.call(that);
      };
      case 1: return function (a) {
        return fn.call(that, a);
      };
      case 2: return function (a, b) {
        return fn.call(that, a, b);
      };
      case 3: return function (a, b, c) {
        return fn.call(that, a, b, c);
      };
    }
    return function (/* ...args */) {
      return fn.apply(that, arguments);
    };
  };

  var fails = function (exec) {
    try {
      return !!exec();
    } catch (e) {
      return true;
    }
  };

  var toString = {}.toString;

  var classofRaw = function (it) {
    return toString.call(it).slice(8, -1);
  };

  // fallback for non-array-like ES3 and non-enumerable old V8 strings


  var split = ''.split;

  var indexedObject = fails(function () {
    // throws an error in rhino, see https://github.com/mozilla/rhino/issues/346
    // eslint-disable-next-line no-prototype-builtins
    return !Object('z').propertyIsEnumerable(0);
  }) ? function (it) {
    return classofRaw(it) == 'String' ? split.call(it, '') : Object(it);
  } : Object;

  // `RequireObjectCoercible` abstract operation
  // https://tc39.github.io/ecma262/#sec-requireobjectcoercible
  var requireObjectCoercible = function (it) {
    if (it == undefined) throw TypeError("Can't call method on " + it);
    return it;
  };

  // `ToObject` abstract operation
  // https://tc39.github.io/ecma262/#sec-toobject
  var toObject = function (argument) {
    return Object(requireObjectCoercible(argument));
  };

  var ceil = Math.ceil;
  var floor = Math.floor;

  // `ToInteger` abstract operation
  // https://tc39.github.io/ecma262/#sec-tointeger
  var toInteger = function (argument) {
    return isNaN(argument = +argument) ? 0 : (argument > 0 ? floor : ceil)(argument);
  };

  var min = Math.min;

  // `ToLength` abstract operation
  // https://tc39.github.io/ecma262/#sec-tolength
  var toLength = function (argument) {
    return argument > 0 ? min(toInteger(argument), 0x1fffffffffffff) : 0; // pow(2, 53) - 1 == 9007199254740991
  };

  var isObject = function (it) {
    return typeof it === 'object' ? it !== null : typeof it === 'function';
  };

  // `IsArray` abstract operation
  // https://tc39.github.io/ecma262/#sec-isarray
  var isArray = Array.isArray || function isArray(arg) {
    return classofRaw(arg) == 'Array';
  };

  function createCommonjsModule(fn, module) {
  	return module = { exports: {} }, fn(module, module.exports), module.exports;
  }

  // https://github.com/zloirock/core-js/issues/86#issuecomment-115759028
  var global = typeof window == 'object' && window && window.Math == Math ? window
    : typeof self == 'object' && self && self.Math == Math ? self
    // eslint-disable-next-line no-new-func
    : Function('return this')();

  // Thank's IE8 for his funny defineProperty
  var descriptors = !fails(function () {
    return Object.defineProperty({}, 'a', { get: function () { return 7; } }).a != 7;
  });

  var document = global.document;
  // typeof document.createElement is 'object' in old IE
  var exist = isObject(document) && isObject(document.createElement);

  var documentCreateElement = function (it) {
    return exist ? document.createElement(it) : {};
  };

  // Thank's IE8 for his funny defineProperty
  var ie8DomDefine = !descriptors && !fails(function () {
    return Object.defineProperty(documentCreateElement('div'), 'a', {
      get: function () { return 7; }
    }).a != 7;
  });

  var anObject = function (it) {
    if (!isObject(it)) {
      throw TypeError(String(it) + ' is not an object');
    } return it;
  };

  // 7.1.1 ToPrimitive(input [, PreferredType])

  // instead of the ES6 spec version, we didn't implement @@toPrimitive case
  // and the second argument - flag - preferred type is a string
  var toPrimitive = function (it, S) {
    if (!isObject(it)) return it;
    var fn, val;
    if (S && typeof (fn = it.toString) == 'function' && !isObject(val = fn.call(it))) return val;
    if (typeof (fn = it.valueOf) == 'function' && !isObject(val = fn.call(it))) return val;
    if (!S && typeof (fn = it.toString) == 'function' && !isObject(val = fn.call(it))) return val;
    throw TypeError("Can't convert object to primitive value");
  };

  var nativeDefineProperty = Object.defineProperty;

  var f = descriptors ? nativeDefineProperty : function defineProperty(O, P, Attributes) {
    anObject(O);
    P = toPrimitive(P, true);
    anObject(Attributes);
    if (ie8DomDefine) try {
      return nativeDefineProperty(O, P, Attributes);
    } catch (e) { /* empty */ }
    if ('get' in Attributes || 'set' in Attributes) throw TypeError('Accessors not supported');
    if ('value' in Attributes) O[P] = Attributes.value;
    return O;
  };

  var objectDefineProperty = {
  	f: f
  };

  var createPropertyDescriptor = function (bitmap, value) {
    return {
      enumerable: !(bitmap & 1),
      configurable: !(bitmap & 2),
      writable: !(bitmap & 4),
      value: value
    };
  };

  var hide = descriptors ? function (object, key, value) {
    return objectDefineProperty.f(object, key, createPropertyDescriptor(1, value));
  } : function (object, key, value) {
    object[key] = value;
    return object;
  };

  var setGlobal = function (key, value) {
    try {
      hide(global, key, value);
    } catch (e) {
      global[key] = value;
    } return value;
  };

  var shared = createCommonjsModule(function (module) {
  var SHARED = '__core-js_shared__';
  var store = global[SHARED] || setGlobal(SHARED, {});

  (module.exports = function (key, value) {
    return store[key] || (store[key] = value !== undefined ? value : {});
  })('versions', []).push({
    version: '3.0.0',
    mode: 'global',
    copyright: '© 2019 Denis Pushkarev (zloirock.ru)'
  });
  });

  var id = 0;
  var postfix = Math.random();

  var uid = function (key) {
    return 'Symbol('.concat(key === undefined ? '' : key, ')_', (++id + postfix).toString(36));
  };

  // Chrome 38 Symbol has incorrect toString conversion
  var nativeSymbol = !fails(function () {
  });

  var store = shared('wks');

  var Symbol = global.Symbol;


  var wellKnownSymbol = function (name) {
    return store[name] || (store[name] = nativeSymbol && Symbol[name]
      || (nativeSymbol ? Symbol : uid)('Symbol.' + name));
  };

  var SPECIES = wellKnownSymbol('species');

  // `ArraySpeciesCreate` abstract operation
  // https://tc39.github.io/ecma262/#sec-arrayspeciescreate
  var arraySpeciesCreate = function (originalArray, length) {
    var C;
    if (isArray(originalArray)) {
      C = originalArray.constructor;
      // cross-realm fallback
      if (typeof C == 'function' && (C === Array || isArray(C.prototype))) C = undefined;
      else if (isObject(C)) {
        C = C[SPECIES];
        if (C === null) C = undefined;
      }
    } return new (C === undefined ? Array : C)(length === 0 ? 0 : length);
  };

  // `Array.prototype.{ forEach, map, filter, some, every, find, findIndex }` methods implementation
  // 0 -> Array#forEach
  // https://tc39.github.io/ecma262/#sec-array.prototype.foreach
  // 1 -> Array#map
  // https://tc39.github.io/ecma262/#sec-array.prototype.map
  // 2 -> Array#filter
  // https://tc39.github.io/ecma262/#sec-array.prototype.filter
  // 3 -> Array#some
  // https://tc39.github.io/ecma262/#sec-array.prototype.some
  // 4 -> Array#every
  // https://tc39.github.io/ecma262/#sec-array.prototype.every
  // 5 -> Array#find
  // https://tc39.github.io/ecma262/#sec-array.prototype.find
  // 6 -> Array#findIndex
  // https://tc39.github.io/ecma262/#sec-array.prototype.findIndex
  var arrayMethods = function (TYPE, specificCreate) {
    var IS_MAP = TYPE == 1;
    var IS_FILTER = TYPE == 2;
    var IS_SOME = TYPE == 3;
    var IS_EVERY = TYPE == 4;
    var IS_FIND_INDEX = TYPE == 6;
    var NO_HOLES = TYPE == 5 || IS_FIND_INDEX;
    var create = specificCreate || arraySpeciesCreate;
    return function ($this, callbackfn, that) {
      var O = toObject($this);
      var self = indexedObject(O);
      var boundFunction = bindContext(callbackfn, that, 3);
      var length = toLength(self.length);
      var index = 0;
      var target = IS_MAP ? create($this, length) : IS_FILTER ? create($this, 0) : undefined;
      var value, result;
      for (;length > index; index++) if (NO_HOLES || index in self) {
        value = self[index];
        result = boundFunction(value, index, O);
        if (TYPE) {
          if (IS_MAP) target[index] = result; // map
          else if (result) switch (TYPE) {
            case 3: return true;              // some
            case 5: return value;             // find
            case 6: return index;             // findIndex
            case 2: target.push(value);       // filter
          } else if (IS_EVERY) return false;  // every
        }
      }
      return IS_FIND_INDEX ? -1 : IS_SOME || IS_EVERY ? IS_EVERY : target;
    };
  };

  var SPECIES$1 = wellKnownSymbol('species');

  var arrayMethodHasSpeciesSupport = function (METHOD_NAME) {
    return !fails(function () {
      var array = [];
      var constructor = array.constructor = {};
      constructor[SPECIES$1] = function () {
        return { foo: 1 };
      };
      return array[METHOD_NAME](Boolean).foo !== 1;
    });
  };

  var nativePropertyIsEnumerable = {}.propertyIsEnumerable;
  var nativeGetOwnPropertyDescriptor = Object.getOwnPropertyDescriptor;

  // Nashorn ~ JDK8 bug
  var NASHORN_BUG = nativeGetOwnPropertyDescriptor && !nativePropertyIsEnumerable.call({ 1: 2 }, 1);

  var f$1 = NASHORN_BUG ? function propertyIsEnumerable(V) {
    var descriptor = nativeGetOwnPropertyDescriptor(this, V);
    return !!descriptor && descriptor.enumerable;
  } : nativePropertyIsEnumerable;

  var objectPropertyIsEnumerable = {
  	f: f$1
  };

  // toObject with fallback for non-array-like ES3 strings



  var toIndexedObject = function (it) {
    return indexedObject(requireObjectCoercible(it));
  };

  var hasOwnProperty = {}.hasOwnProperty;

  var has = function (it, key) {
    return hasOwnProperty.call(it, key);
  };

  var nativeGetOwnPropertyDescriptor$1 = Object.getOwnPropertyDescriptor;

  var f$2 = descriptors ? nativeGetOwnPropertyDescriptor$1 : function getOwnPropertyDescriptor(O, P) {
    O = toIndexedObject(O);
    P = toPrimitive(P, true);
    if (ie8DomDefine) try {
      return nativeGetOwnPropertyDescriptor$1(O, P);
    } catch (e) { /* empty */ }
    if (has(O, P)) return createPropertyDescriptor(!objectPropertyIsEnumerable.f.call(O, P), O[P]);
  };

  var objectGetOwnPropertyDescriptor = {
  	f: f$2
  };

  var functionToString = shared('native-function-to-string', Function.toString);

  var WeakMap = global.WeakMap;

  var nativeWeakMap = typeof WeakMap === 'function' && /native code/.test(functionToString.call(WeakMap));

  var shared$1 = shared('keys');


  var sharedKey = function (key) {
    return shared$1[key] || (shared$1[key] = uid(key));
  };

  var hiddenKeys = {};

  var WeakMap$1 = global.WeakMap;
  var set, get, has$1;

  var enforce = function (it) {
    return has$1(it) ? get(it) : set(it, {});
  };

  var getterFor = function (TYPE) {
    return function (it) {
      var state;
      if (!isObject(it) || (state = get(it)).type !== TYPE) {
        throw TypeError('Incompatible receiver, ' + TYPE + ' required');
      } return state;
    };
  };

  if (nativeWeakMap) {
    var store$1 = new WeakMap$1();
    var wmget = store$1.get;
    var wmhas = store$1.has;
    var wmset = store$1.set;
    set = function (it, metadata) {
      wmset.call(store$1, it, metadata);
      return metadata;
    };
    get = function (it) {
      return wmget.call(store$1, it) || {};
    };
    has$1 = function (it) {
      return wmhas.call(store$1, it);
    };
  } else {
    var STATE = sharedKey('state');
    hiddenKeys[STATE] = true;
    set = function (it, metadata) {
      hide(it, STATE, metadata);
      return metadata;
    };
    get = function (it) {
      return has(it, STATE) ? it[STATE] : {};
    };
    has$1 = function (it) {
      return has(it, STATE);
    };
  }

  var internalState = {
    set: set,
    get: get,
    has: has$1,
    enforce: enforce,
    getterFor: getterFor
  };

  var redefine = createCommonjsModule(function (module) {
  var getInternalState = internalState.get;
  var enforceInternalState = internalState.enforce;
  var TEMPLATE = String(functionToString).split('toString');

  shared('inspectSource', function (it) {
    return functionToString.call(it);
  });

  (module.exports = function (O, key, value, options) {
    var unsafe = options ? !!options.unsafe : false;
    var simple = options ? !!options.enumerable : false;
    var noTargetGet = options ? !!options.noTargetGet : false;
    if (typeof value == 'function') {
      if (typeof key == 'string' && !has(value, 'name')) hide(value, 'name', key);
      enforceInternalState(value).source = TEMPLATE.join(typeof key == 'string' ? key : '');
    }
    if (O === global) {
      if (simple) O[key] = value;
      else setGlobal(key, value);
      return;
    } else if (!unsafe) {
      delete O[key];
    } else if (!noTargetGet && O[key]) {
      simple = true;
    }
    if (simple) O[key] = value;
    else hide(O, key, value);
  // add fake Function#toString for correct work wrapped methods / constructors with methods like LoDash isNative
  })(Function.prototype, 'toString', function toString() {
    return typeof this == 'function' && getInternalState(this).source || functionToString.call(this);
  });
  });

  var max = Math.max;
  var min$1 = Math.min;

  // Helper for a popular repeating case of the spec:
  // Let integer be ? ToInteger(index).
  // If integer < 0, let result be max((length + integer), 0); else let result be min(length, length).
  var toAbsoluteIndex = function (index, length) {
    var integer = toInteger(index);
    return integer < 0 ? max(integer + length, 0) : min$1(integer, length);
  };

  // `Array.prototype.{ indexOf, includes }` methods implementation
  // false -> Array#indexOf
  // https://tc39.github.io/ecma262/#sec-array.prototype.indexof
  // true  -> Array#includes
  // https://tc39.github.io/ecma262/#sec-array.prototype.includes
  var arrayIncludes = function (IS_INCLUDES) {
    return function ($this, el, fromIndex) {
      var O = toIndexedObject($this);
      var length = toLength(O.length);
      var index = toAbsoluteIndex(fromIndex, length);
      var value;
      // Array#includes uses SameValueZero equality algorithm
      // eslint-disable-next-line no-self-compare
      if (IS_INCLUDES && el != el) while (length > index) {
        value = O[index++];
        // eslint-disable-next-line no-self-compare
        if (value != value) return true;
      // Array#indexOf ignores holes, Array#includes - not
      } else for (;length > index; index++) if (IS_INCLUDES || index in O) {
        if (O[index] === el) return IS_INCLUDES || index || 0;
      } return !IS_INCLUDES && -1;
    };
  };

  var arrayIndexOf = arrayIncludes(false);


  var objectKeysInternal = function (object, names) {
    var O = toIndexedObject(object);
    var i = 0;
    var result = [];
    var key;
    for (key in O) !has(hiddenKeys, key) && has(O, key) && result.push(key);
    // Don't enum bug & hidden keys
    while (names.length > i) if (has(O, key = names[i++])) {
      ~arrayIndexOf(result, key) || result.push(key);
    }
    return result;
  };

  // IE8- don't enum bug keys
  var enumBugKeys = [
    'constructor',
    'hasOwnProperty',
    'isPrototypeOf',
    'propertyIsEnumerable',
    'toLocaleString',
    'toString',
    'valueOf'
  ];

  // 19.1.2.7 / 15.2.3.4 Object.getOwnPropertyNames(O)

  var hiddenKeys$1 = enumBugKeys.concat('length', 'prototype');

  var f$3 = Object.getOwnPropertyNames || function getOwnPropertyNames(O) {
    return objectKeysInternal(O, hiddenKeys$1);
  };

  var objectGetOwnPropertyNames = {
  	f: f$3
  };

  var f$4 = Object.getOwnPropertySymbols;

  var objectGetOwnPropertySymbols = {
  	f: f$4
  };

  var Reflect = global.Reflect;

  // all object keys, includes non-enumerable and symbols
  var ownKeys = Reflect && Reflect.ownKeys || function ownKeys(it) {
    var keys = objectGetOwnPropertyNames.f(anObject(it));
    var getOwnPropertySymbols = objectGetOwnPropertySymbols.f;
    return getOwnPropertySymbols ? keys.concat(getOwnPropertySymbols(it)) : keys;
  };

  var copyConstructorProperties = function (target, source) {
    var keys = ownKeys(source);
    var defineProperty = objectDefineProperty.f;
    var getOwnPropertyDescriptor = objectGetOwnPropertyDescriptor.f;
    for (var i = 0; i < keys.length; i++) {
      var key = keys[i];
      if (!has(target, key)) defineProperty(target, key, getOwnPropertyDescriptor(source, key));
    }
  };

  var replacement = /#|\.prototype\./;

  var isForced = function (feature, detection) {
    var value = data[normalize(feature)];
    return value == POLYFILL ? true
      : value == NATIVE ? false
      : typeof detection == 'function' ? fails(detection)
      : !!detection;
  };

  var normalize = isForced.normalize = function (string) {
    return String(string).replace(replacement, '.').toLowerCase();
  };

  var data = isForced.data = {};
  var NATIVE = isForced.NATIVE = 'N';
  var POLYFILL = isForced.POLYFILL = 'P';

  var isForced_1 = isForced;

  var getOwnPropertyDescriptor = objectGetOwnPropertyDescriptor.f;






  /*
    options.target      - name of the target object
    options.global      - target is the global object
    options.stat        - export as static methods of target
    options.proto       - export as prototype methods of target
    options.real        - real prototype method for the `pure` version
    options.forced      - export even if the native feature is available
    options.bind        - bind methods to the target, required for the `pure` version
    options.wrap        - wrap constructors to preventing global pollution, required for the `pure` version
    options.unsafe      - use the simple assignment of property instead of delete + defineProperty
    options.sham        - add a flag to not completely full polyfills
    options.enumerable  - export as enumerable property
    options.noTargetGet - prevent calling a getter on target
  */
  var _export = function (options, source) {
    var TARGET = options.target;
    var GLOBAL = options.global;
    var STATIC = options.stat;
    var FORCED, target, key, targetProperty, sourceProperty, descriptor;
    if (GLOBAL) {
      target = global;
    } else if (STATIC) {
      target = global[TARGET] || setGlobal(TARGET, {});
    } else {
      target = (global[TARGET] || {}).prototype;
    }
    if (target) for (key in source) {
      sourceProperty = source[key];
      if (options.noTargetGet) {
        descriptor = getOwnPropertyDescriptor(target, key);
        targetProperty = descriptor && descriptor.value;
      } else targetProperty = target[key];
      FORCED = isForced_1(GLOBAL ? key : TARGET + (STATIC ? '.' : '#') + key, options.forced);
      // contained in target
      if (!FORCED && targetProperty !== undefined) {
        if (typeof sourceProperty === typeof targetProperty) continue;
        copyConstructorProperties(sourceProperty, targetProperty);
      }
      // add a flag to not completely full polyfills
      if (options.sham || (targetProperty && targetProperty.sham)) {
        hide(sourceProperty, 'sham', true);
      }
      // extend global
      redefine(target, key, sourceProperty, options);
    }
  };

  var internalFilter = arrayMethods(2);

  var SPECIES_SUPPORT = arrayMethodHasSpeciesSupport('filter');

  // `Array.prototype.filter` method
  // https://tc39.github.io/ecma262/#sec-array.prototype.filter
  // with adding support of @@species
  _export({ target: 'Array', proto: true, forced: !SPECIES_SUPPORT }, {
    filter: function filter(callbackfn /* , thisArg */) {
      return internalFilter(this, callbackfn, arguments[1]);
    }
  });

  var sloppyArrayMethod = function (METHOD_NAME, argument) {
    var method = [][METHOD_NAME];
    return !method || !fails(function () {
      // eslint-disable-next-line no-useless-call
      method.call(null, argument || function () { throw Error(); }, 1);
    });
  };

  var internalIndexOf = arrayIncludes(false);
  var nativeIndexOf = [].indexOf;

  var NEGATIVE_ZERO = !!nativeIndexOf && 1 / [1].indexOf(1, -0) < 0;
  var SLOPPY_METHOD = sloppyArrayMethod('indexOf');

  // `Array.prototype.indexOf` method
  // https://tc39.github.io/ecma262/#sec-array.prototype.indexof
  _export({ target: 'Array', proto: true, forced: NEGATIVE_ZERO || SLOPPY_METHOD }, {
    indexOf: function indexOf(searchElement /* , fromIndex = 0 */) {
      return NEGATIVE_ZERO
        // convert -0 to +0
        ? nativeIndexOf.apply(this, arguments) || 0
        : internalIndexOf(this, searchElement, arguments[1]);
    }
  });

  var createProperty = function (object, key, value) {
    var propertyKey = toPrimitive(key);
    if (propertyKey in object) objectDefineProperty.f(object, propertyKey, createPropertyDescriptor(0, value));
    else object[propertyKey] = value;
  };

  var SPECIES$2 = wellKnownSymbol('species');
  var nativeSlice = [].slice;
  var max$1 = Math.max;

  var SPECIES_SUPPORT$1 = arrayMethodHasSpeciesSupport('slice');

  // `Array.prototype.slice` method
  // https://tc39.github.io/ecma262/#sec-array.prototype.slice
  // fallback for not array-like ES3 strings and DOM objects
  _export({ target: 'Array', proto: true, forced: !SPECIES_SUPPORT$1 }, {
    slice: function slice(start, end) {
      var O = toIndexedObject(this);
      var length = toLength(O.length);
      var k = toAbsoluteIndex(start, length);
      var fin = toAbsoluteIndex(end === undefined ? length : end, length);
      // inline `ArraySpeciesCreate` for usage native `Array#slice` where it's possible
      var Constructor, result, n;
      if (isArray(O)) {
        Constructor = O.constructor;
        // cross-realm fallback
        if (typeof Constructor == 'function' && (Constructor === Array || isArray(Constructor.prototype))) {
          Constructor = undefined;
        } else if (isObject(Constructor)) {
          Constructor = Constructor[SPECIES$2];
          if (Constructor === null) Constructor = undefined;
        }
        if (Constructor === Array || Constructor === undefined) {
          return nativeSlice.call(O, k, fin);
        }
      }
      result = new (Constructor === undefined ? Array : Constructor)(max$1(fin - k, 0));
      for (n = 0; k < fin; k++, n++) if (k in O) createProperty(result, n, O[k]);
      result.length = n;
      return result;
    }
  });

  // a string of all valid unicode whitespaces
  // eslint-disable-next-line max-len
  var whitespaces = '\x09\x0A\x0B\x0C\x0D\x20\xA0\u1680\u2000\u2001\u2002\u2003\u2004\u2005\u2006\u2007\u2008\u2009\u200A\u202F\u205F\u3000\u2028\u2029\uFEFF';

  var whitespace = '[' + whitespaces + ']';
  var ltrim = RegExp('^' + whitespace + whitespace + '*');
  var rtrim = RegExp(whitespace + whitespace + '*$');

  // 1 -> String#trimStart
  // 2 -> String#trimEnd
  // 3 -> String#trim
  var stringTrim = function (string, TYPE) {
    string = String(requireObjectCoercible(string));
    if (TYPE & 1) string = string.replace(ltrim, '');
    if (TYPE & 2) string = string.replace(rtrim, '');
    return string;
  };

  var nativeParseInt = global.parseInt;


  var hex = /^[-+]?0[xX]/;
  var FORCED = nativeParseInt(whitespaces + '08') !== 8 || nativeParseInt(whitespaces + '0x16') !== 22;

  var _parseInt = FORCED ? function parseInt(str, radix) {
    var string = stringTrim(String(str), 3);
    return nativeParseInt(string, (radix >>> 0) || (hex.test(string) ? 16 : 10));
  } : nativeParseInt;

  // `parseInt` method
  // https://tc39.github.io/ecma262/#sec-parseint-string-radix
  _export({ global: true, forced: parseInt != _parseInt }, {
    parseInt: _parseInt
  });

  /**
   * @author doug-the-guy
   * @version v1.0.0
   *
   * Bootstrap Table Pipeline
   * -----------------------
   *
   * This plugin enables client side data caching for server side requests which will
   * eliminate the need to issue a new request every page change. This will allow
   * for a performance balance for a large data set between returning all data at once
   * (client side paging) and a new server side request (server side paging).
   *
   * There are two new options:
   *  - usePipeline: enables this feature
   *  - pipelineSize: the size of each cache window
   *
   * The size of the pipeline must be evenly divisible by the current page size. This is
   * assured by rounding up to the nearest evenly divisible value. For example, if
   * the pipeline size is 4990 and the current page size is 25, then pipeline size will
   * be dynamically set to 5000.
   *
   * The cache windows are computed based on the pipeline size and the total number of rows
   * returned by the server side query. For example, with pipeline size 500 and total rows
   * 1300, the cache windows will be:
   *
   *  [{'lower': 0, 'upper': 499}, {'lower': 500, 'upper': 999}, {'lower': 1000, 'upper': 1499}]
   *
   * Using the limit (i.e. the pipelineSize) and offset parameters, the server side request
   * **MUST** return only the data in the requested cache window **AND** the total number of rows.
   * To wit, the server side code must use the offset and limit parameters to prepare the response
   * data.
   *
   * On a page change, the new offset is checked if it is within the current cache window. If so,
   * the requested page data is returned from the cached data set. Otherwise, a new server side
   * request will be issued for the new cache window.
   *
   * The current cached data is only invalidated on these events:
   *  * sorting
   *  * searching
   *  * page size change
   *  * page change moves into a new cache window
   *
   * There are two new events:
   *  - cached-data-hit.bs.table: issued when cached data is used on a page change
   *  - cached-data-reset.bs.table: issued when the cached data is invalidated and a
   *      new server side request is issued
   *
   **/

  var Utils = $.fn.bootstrapTable.utils;
  $.extend($.fn.bootstrapTable.defaults, {
    usePipeline: false,
    pipelineSize: 1000,
    onCachedDataHit: function onCachedDataHit(data) {
      return false;
    },
    onCachedDataReset: function onCachedDataReset(data) {
      return false;
    }
  });
  $.extend($.fn.bootstrapTable.Constructor.EVENTS, {
    'cached-data-hit.bs.table': 'onCachedDataHit',
    'cached-data-reset.bs.table': 'onCachedDataReset'
  });
  var BootstrapTable = $.fn.bootstrapTable.Constructor;
  var _init = BootstrapTable.prototype.init;
  var _initServer = BootstrapTable.prototype.initServer;
  var _onSearch = BootstrapTable.prototype.onSearch;
  var _onSort = BootstrapTable.prototype.onSort;
  var _onPageListChange = BootstrapTable.prototype.onPageListChange;

  BootstrapTable.prototype.init = function () {
    // needs to be called before initServer()
    this.initPipeline();

    for (var _len = arguments.length, args = new Array(_len), _key = 0; _key < _len; _key++) {
      args[_key] = arguments[_key];
    }

    _init.apply(this, Array.prototype.slice.apply(args));
  };

  BootstrapTable.prototype.initPipeline = function () {
    this.cacheRequestJSON = {};
    this.cacheWindows = [];
    this.currWindow = 0;
    this.resetCache = true;
  };

  BootstrapTable.prototype.onSearch = function (event) {
    /* force a cache reset on search */
    if (this.options.usePipeline) {
      this.resetCache = true;
    }

    _onSearch.apply(this, Array.prototype.slice.apply(arguments));
  };

  BootstrapTable.prototype.onSort = function (event) {
    /* force a cache reset on sort */
    if (this.options.usePipeline) {
      this.resetCache = true;
    }

    _onSort.apply(this, Array.prototype.slice.apply(arguments));
  };

  BootstrapTable.prototype.onPageListChange = function (event) {
    /* rebuild cache window on page size change */
    var target = $(event.currentTarget);
    var newPageSize = parseInt(target.text());
    this.options.pipelineSize = this.calculatePipelineSize(this.options.pipelineSize, newPageSize);
    this.resetCache = true;

    _onPageListChange.apply(this, Array.prototype.slice.apply(arguments));
  };

  BootstrapTable.prototype.calculatePipelineSize = function (pipelineSize, pageSize) {
    /* calculate pipeline size by rounding up to the nearest value evenly divisible
          * by the pageSize */
    if (pageSize === 0) return 0;
    return Math.ceil(pipelineSize / pageSize) * pageSize;
  };

  BootstrapTable.prototype.setCacheWindows = function () {
    /* set cache windows based on the total number of rows returned by server side
          * request and the pipelineSize */
    this.cacheWindows = [];
    var numWindows = this.options.totalRows / this.options.pipelineSize;

    for (var i = 0; i <= numWindows; i++) {
      var b = i * this.options.pipelineSize;
      this.cacheWindows[i] = {
        'lower': b,
        'upper': b + this.options.pipelineSize - 1
      };
    }
  };

  BootstrapTable.prototype.setCurrWindow = function (offset) {
    /* set the current cache window index, based on where the current offset falls */
    this.currWindow = 0;

    for (var i = 0; i < this.cacheWindows.length; i++) {
      if (this.cacheWindows[i].lower <= offset && offset <= this.cacheWindows[i].upper) {
        this.currWindow = i;
        break;
      }
    }
  };

  BootstrapTable.prototype.drawFromCache = function (offset, limit) {
    /* draw rows from the cache using offset and limit */
    var res = $.extend(true, {}, this.cacheRequestJSON);
    var drawStart = offset - this.cacheWindows[this.currWindow].lower;
    var drawEnd = drawStart + limit;
    res.rows = res.rows.slice(drawStart, drawEnd);
    return res;
  };

  BootstrapTable.prototype.initServer = function (silent, query, url) {
    /* determine if requested data is in cache (on paging) or if
          * a new ajax request needs to be issued (sorting, searching, paging
          * moving outside of cached data, page size change)
          * initial version of this extension will entirely override base initServer
          **/
    var data = {};
    var index = this.header.fields.indexOf(this.options.sortName);
    var params = {
      searchText: this.searchText,
      sortName: this.options.sortName,
      sortOrder: this.options.sortOrder
    };
    var request = null;

    if (this.header.sortNames[index]) {
      params.sortName = this.header.sortNames[index];
    }

    if (this.options.pagination && this.options.sidePagination === 'server') {
      params.pageSize = this.options.pageSize === this.options.formatAllRows() ? this.options.totalRows : this.options.pageSize;
      params.pageNumber = this.options.pageNumber;
    }

    if (!(url || this.options.url) && !this.options.ajax) {
      return;
    }

    var useAjax = true;

    if (this.options.queryParamsType === 'limit') {
      params = {
        searchText: params.searchText,
        sortName: params.sortName,
        sortOrder: params.sortOrder
      };

      if (this.options.pagination && this.options.sidePagination === 'server') {
        params.limit = this.options.pageSize === this.options.formatAllRows() ? this.options.totalRows : this.options.pageSize;
        params.offset = (this.options.pageSize === this.options.formatAllRows() ? this.options.totalRows : this.options.pageSize) * (this.options.pageNumber - 1);

        if (this.options.usePipeline) {
          // if cacheWindows is empty, this is the initial request
          if (!this.cacheWindows.length) {
            useAjax = true;
            params.drawOffset = params.offset; // cache exists: determine if the page request is entirely within the current cached window
          } else {
            var w = this.cacheWindows[this.currWindow]; // case 1: reset cache but stay within current window (e.g. column sort)
            // case 2: move outside of the current window (e.g. search or paging)
            //  since each cache window is aligned with the current page size
            //  checking if params.offset is outside the current window is sufficient.
            //  need to requery for preceding or succeeding cache window
            //  also handle case

            if (this.resetCache || params.offset < w.lower || params.offset > w.upper) {
              useAjax = true;
              this.setCurrWindow(params.offset); // store the relative offset for drawing the page data afterwards

              params.drawOffset = params.offset; // now set params.offset to the lower bound of the new cache window
              // the server will return that whole cache window

              params.offset = this.cacheWindows[this.currWindow].lower; // within current cache window
            } else {
              useAjax = false;
            }
          }
        } else {
          if (params.limit === 0) {
            delete params.limit;
          }
        }
      }
    } // force an ajax call - this is on search, sort or page size change


    if (this.resetCache) {
      useAjax = true;
      this.resetCache = false;
    }

    if (this.options.usePipeline && useAjax) {
      /* in this scenario limit is used on the server to get the cache window
              * and drawLimit is used to get the page data afterwards */
      params.drawLimit = params.limit;
      params.limit = this.options.pipelineSize;
    } // cached results can be used


    if (!useAjax) {
      var res = this.drawFromCache(params.offset, params.limit);
      this.load(res);
      this.trigger('load-success', res);
      this.trigger('cached-data-hit', res);
      return;
    } // cached results can't be used
    // continue base initServer code


    if (!$.isEmptyObject(this.filterColumnsPartial)) {
      params.filter = JSON.stringify(this.filterColumnsPartial, null);
    }

    data = Utils.calculateObjectValue(this.options, this.options.queryParams, [params], data);
    $.extend(data, query || {}); // false to stop request

    if (data === false) {
      return;
    }

    if (!silent) {
      this.$tableLoading.show();
    }

    var self = this;
    request = $.extend({}, Utils.calculateObjectValue(null, this.options.ajaxOptions), {
      type: this.options.method,
      url: url || this.options.url,
      data: this.options.contentType === 'application/json' && this.options.method === 'post' ? JSON.stringify(data) : data,
      cache: this.options.cache,
      contentType: this.options.contentType,
      dataType: this.options.dataType,
      success: function success(res) {
        res = Utils.calculateObjectValue(self.options, self.options.responseHandler, [res], res); // cache results if using pipelining

        if (self.options.usePipeline) {
          // store entire request in cache
          self.cacheRequestJSON = $.extend(true, {}, res); // this gets set in load() also but needs to be set before
          // setting cacheWindows

          self.options.totalRows = res[self.options.totalField]; // if this is a search, potentially less results will be returned
          // so cache windows need to be rebuilt. Otherwise it
          // will come out the same

          self.setCacheWindows();
          self.setCurrWindow(params.drawOffset); // just load data for the page

          res = self.drawFromCache(params.drawOffset, params.drawLimit);
          self.trigger('cached-data-reset', res);
        }

        self.load(res);
        self.trigger('load-success', res);
        if (!silent) self.$tableLoading.hide();
      },
      error: function error(res) {
        var data = [];

        if (self.options.sidePagination === 'server') {
          data = {};
          data[self.options.totalField] = 0;
          data[self.options.dataField] = [];
        }

        self.load(data);
        self.trigger('load-error', res.status, res);
        if (!silent) self.$tableLoading.hide();
      }
    });

    if (this.options.ajax) {
      Utils.calculateObjectValue(this, this.options.ajax, [request], null);
    } else {
      if (this._xhr && this._xhr.readyState !== 4) {
        this._xhr.abort();
      }

      this._xhr = $.ajax(request);
    }
  };

}));
