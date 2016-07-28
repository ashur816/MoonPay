!function (t) {
    function e(i) {
        if (n[i])return n[i].exports;
        var r = n[i] = {exports: {}, id: i, loaded: !1};
        return t[i].call(r.exports, r, r.exports, e), r.loaded = !0, r.exports
    }

    var n = {};
    return e.m = t, e.c = n, e.p = "", e(0)
}([function (t, e, n) {
    t.exports = n(3)
}, function (t, e) {
}, , function (t, e, n) {
    var i = n(8), r = n(4);
    n(7), n(5), n(6), i(function () {
        function t(t) {
            for (var e = a.value.length, n = u.length; n--;)u[n].style.visibility = e > n ? "visible" : "hidden", u[n].innerHTML = "";
            t && c.length < e && e > 0 && (u[e - 1].innerHTML = ""), c = a.value
        }

        function e() {
            var t = a.value.length;
            try {
                a.setSelectionRange(t, t)
            } catch (e) {
            }
        }

        window.showMsg = function (t) {
            Zepto.dialog.show({
                message: t, buttons: ["\u786e\u5b9a"], complete: function (t) {
                    Zepto.dialog.hide()
                }
            })
        };
        var n = 0;
        i("body").children().each(function (t) {
            elementHieght = i(this).height(), (i(this).hasClass("am-dialog-mask") || i(this).hasClass("am-dialog-box") || i(this).hasClass("am-loading")) && (elementHieght = 0), n += elementHieght
        });
        var r = function () {
            n > i(window).height() - 50 ? i(".moon-logo").css({
                position: "relative",
                left: "auto",
                "margin-left": "auto",
                "margin-top": "30px",
                "margin-bottom": "-25px"
            }) : i(".moon-logo").css({position: "absolute", left: "50%", "margin-left": "-35px", "margin-top": "10px", "margin-bottom": "auto"})
        };
        if (r(), window.addEventListener("resize", function () {
                r()
            }, !1), i(".am-password-handy").length > 0) {
            var o = document.querySelector(".am-password-handy"), a = o.querySelector("input"), s = o.querySelector(".am-password-handy-security"), u = s.querySelectorAll("li i"), c = "";
            o.display = "-webkit-box!important", a.value.length > 0 && t(!1), o.addEventListener("click", function () {
                a.focus()
            }, !1), a.addEventListener("focus", function () {
                setTimeout(e, 0)
            }, !1), a.addEventListener("keyup", function (e) {
                var n = e.which, i = !0;
                (8 === n || 46 === n) && (i = !1), t(i)
            }, !1), a.addEventListener("input", function (e) {
                t(!1)
            }, !1)
        }
        i(function () {
            var t = null, e = function (t) {
                setTimeout(function () {
                    t && t.tagName && "INPUT" == t.tagName && t.setSelectionRange(99, 99)
                }, 10)
            };
            i(document).on("keydown", function (n) {
                if (keycode = n.keyCode || n.which, i(".am-dialog-box:visible").length > 0)var r = i(".am-dialog-box a:visible").sort(function (t, e) {
                    return i(t).attr("tabindex") - i(e).attr("tabindex")
                }); else if (i(".protocol-content:visible").length > 0)var r = i(".protocol-content a:visible").sort(function (t, e) {
                    return i(t).attr("tabindex") - i(e).attr("tabindex")
                }); else var r = i("a, input, button, select :visible").sort(function (t, e) {
                    return i(t).attr("tabindex") - i(e).attr("tabindex")
                });
                39 == keycode || 40 == keycode ? null != t && t < r.length - 1 ? (r[++t].focus(), e(r[t])) : (t = 0, r[t].focus()) : (37 == keycode || 38 == keycode) && (null != t && t > 0 && t <= r.length ? (r[--t].focus(), e(r[t])) : (t = r.length - 1, r[t].focus()))
            })
        })
    }), t.exports = {$: i, dialog: r}
}, function (t, e, n) {
    "use strict";
    var i = window.Zepto;
    n(1), i.dialog = {}, i.extend(i.dialog, {
        options: {}, optionsDefaults: {title: "", message: "", messageAlign: "center", buttons: ["\u786e\u5b9a"], complete: null}, setTemplate: function () {
            var t = i.dialog.options, e = "" != t.title ? '<div class="am-dialog-title">' + t.title + "</div>" : "", n = "" != t.message ? '<div class="am-dialog-text" style="text-align:' + t.messageAlign + ';">' + t.message + "</div>" : "", r = function (t) {
                var e = "";
                if (t.constructor == Array)for (var e = 2 == t.length ? 'am-mode="two"' : "", n = "", i = 0; i < t.length; i++)n += '<a href="#">' + t[i] + "</a>"; else if (t.constructor == String)var n = '<a href="#">' + t + "</a>";
                return '<div class="am-dialog-button" ' + e + ">" + n + "</div>"
            }, o = '<div class="am-dialog-mask"></div><div class="am-dialog-box">    <div class="am-dialog-content">' + e + n + r(t.buttons) + "    </div></div>";
            return o
        }, setOptions: function (t) {
            i.dialog.options = i.extend({}, i.dialog.optionsDefaults, t || {})
        }, show: function (t) {
            i.dialog.setOptions(t), (i("body").find(".am-dialog-mask").length > 0 || i("body").find(".am-dialog-box") > 0) && i.dialog.hide(), i("body").append(i.dialog.setTemplate(i.dialog.options)), i("body").find(".am-dialog-box a").each(function (t) {
                i(this).on("click", function (e) {
                    var n = {};
                    n.element = this, n.order = t + 1, "function" == typeof i.dialog.options.complete && i.dialog.options.complete(n), e.preventDefault()
                })
            }), i("body").find(".am-dialog-box").css("margin-top", -i("body").find(".am-dialog-box").height() / 2.8).animate({opacity: 1, scale: 1}, 400, "ease-out")
        }, hide: function () {
            i("body").find(".am-dialog-mask").animate({opacity: 0}, 400, "ease-out", function () {
                i(this).css("display", "none")
            }), i("body").find(".am-dialog-box").animate({opacity: 0}, 400, "ease-out", function () {
                i(this).css("display", "none")
            }), window.setTimeout(function () {
                i("body").find(".am-dialog-mask").remove(), i("body").find(".am-dialog-box").remove()
            }, 500)
        }
    }), t.exports = i.dialog
}, function (t, e) {
    !function (t, e) {
        function n(t) {
            return t.replace(/([a-z])([A-Z])/, "$1-$2").toLowerCase()
        }

        function i(t) {
            return r ? r + t : t.toLowerCase()
        }

        var r, o, a, s, u, c, l, f, h, d, p = "", m = {
            Webkit: "webkit",
            Moz: "",
            O: "o"
        }, g = window.document, v = g.createElement("div"), y = /^((translate|rotate|scale)(X|Y|Z|3d)?|matrix(3d)?|perspective|skew(X|Y)?)$/i, x = {};
        t.each(m, function (t, n) {
            return v.style[t + "TransitionProperty"] !== e ? (p = "-" + t.toLowerCase() + "-", r = n, !1) : void 0
        }), o = p + "transform", x[a = p + "transition-property"] = x[s = p + "transition-duration"] = x[c = p + "transition-delay"] = x[u = p + "transition-timing-function"] = x[l = p + "animation-name"] = x[f = p + "animation-duration"] = x[d = p + "animation-delay"] = x[h = p + "animation-timing-function"] = "", t.fx = {
            off: r === e && v.style.transitionProperty === e,
            speeds: {_default: 400, fast: 200, slow: 600},
            cssPrefix: p,
            transitionEnd: i("TransitionEnd"),
            animationEnd: i("AnimationEnd")
        }, t.fn.animate = function (n, i, r, o, a) {
            return t.isFunction(i) && (o = i, r = e, i = e), t.isFunction(r) && (o = r, r = e), t.isPlainObject(i) && (r = i.easing, o = i.complete, a = i.delay, i = i.duration), i && (i = ("number" == typeof i ? i : t.fx.speeds[i] || t.fx.speeds._default) / 1e3), a && (a = parseFloat(a) / 1e3), this.anim(n, i, r, o, a)
        }, t.fn.anim = function (i, r, p, m, g) {
            var v, b, w, E = {}, T = "", j = this, C = t.fx.transitionEnd, S = !1;
            if (r === e && (r = t.fx.speeds._default / 1e3), g === e && (g = 0), t.fx.off && (r = 0), "string" == typeof i)E[l] = i, E[f] = r + "s", E[d] = g + "s", E[h] = p || "linear", C = t.fx.animationEnd; else {
                b = [];
                for (v in i)y.test(v) ? T += v + "(" + i[v] + ") " : (E[v] = i[v], b.push(n(v)));
                T && (E[o] = T, b.push(o)), r > 0 && "object" == typeof i && (E[a] = b.join(", "), E[s] = r + "s", E[c] = g + "s", E[u] = p || "linear")
            }
            return w = function (e) {
                if ("undefined" != typeof e) {
                    if (e.target !== e.currentTarget)return;
                    t(e.target).unbind(C, w)
                } else t(this).unbind(C, w);
                S = !0, t(this).css(x), m && m.call(this)
            }, r > 0 && (this.bind(C, w), setTimeout(function () {
                S || w.call(j)
            }, 1e3 * r + 25)), this.size() && this.get(0).clientLeft, this.css(E), 0 >= r && setTimeout(function () {
                j.each(function () {
                    w.call(this)
                })
            }, 0), this
        }, v = null
    }(Zepto)
}, function (t, e) {
    !function (t, e) {
        function n(n, i, r, o, a) {
            "function" != typeof i || a || (a = i, i = e);
            var s = {opacity: r};
            return o && (s.scale = o, n.css(t.fx.cssPrefix + "transform-origin", "0 0")), n.animate(s, i, null, a)
        }

        function i(e, i, r, o) {
            return n(e, i, 0, r, function () {
                a.call(t(this)), o && o.call(this)
            })
        }

        var r = window.document, o = (r.documentElement, t.fn.show), a = t.fn.hide, s = t.fn.toggle;
        t.fn.show = function (t, i) {
            return o.call(this), t === e ? t = 0 : this.css("opacity", 0), n(this, t, 1, "1,1", i)
        }, t.fn.hide = function (t, n) {
            return t === e ? a.call(this) : i(this, t, "0,0", n)
        }, t.fn.toggle = function (n, i) {
            return n === e || "boolean" == typeof n ? s.call(this, n) : this.each(function () {
                var e = t(this);
                e["none" == e.css("display") ? "show" : "hide"](n, i)
            })
        }, t.fn.fadeTo = function (t, e, i) {
            return n(this, t, e, null, i)
        }, t.fn.fadeIn = function (t, e) {
            var n = this.css("opacity");
            return n > 0 ? this.css("opacity", 0) : n = 1, o.call(this).fadeTo(t, n, e)
        }, t.fn.fadeOut = function (t, e) {
            return i(this, t, null, e)
        }, t.fn.fadeToggle = function (e, n) {
            return this.each(function () {
                var i = t(this);
                i[0 == i.css("opacity") || "none" == i.css("display") ? "fadeIn" : "fadeOut"](e, n)
            })
        }
    }(Zepto)
}, function (t, e) {
    !function (t) {
        function e(e) {
            return e = t(e), !(!e.width() && !e.height()) && "none" !== e.css("display")
        }

        function n(t, e) {
            t = t.replace(/=#\]/g, '="#"]');
            var n, i, r = s.exec(t);
            if (r && r[2] in a && (n = a[r[2]], i = r[3], t = r[1], i)) {
                var o = Number(i);
                i = isNaN(o) ? i.replace(/^["']|["']$/g, "") : o
            }
            return e(t, n, i)
        }

        var i = t.zepto, r = i.qsa, o = i.matches, a = t.expr[":"] = {
            visible: function () {
                return e(this) ? this : void 0
            }, hidden: function () {
                return e(this) ? void 0 : this
            }, selected: function () {
                return this.selected ? this : void 0
            }, checked: function () {
                return this.checked ? this : void 0
            }, parent: function () {
                return this.parentNode
            }, first: function (t) {
                return 0 === t ? this : void 0
            }, last: function (t, e) {
                return t === e.length - 1 ? this : void 0
            }, eq: function (t, e, n) {
                return t === n ? this : void 0
            }, contains: function (e, n, i) {
                return t(this).text().indexOf(i) > -1 ? this : void 0
            }, has: function (t, e, n) {
                return i.qsa(this, n).length ? this : void 0
            }
        }, s = new RegExp("(.*):(\\w+)(?:\\(([^)]+)\\))?$\\s*"), u = /^\s*>/, c = "Zepto" + +new Date;
        i.qsa = function (e, o) {
            return n(o, function (n, a, s) {
                try {
                    var l;
                    !n && a ? n = "*" : u.test(n) && (l = t(e).addClass(c), n = "." + c + " " + n);
                    var f = r(e, n)
                } catch (h) {
                    throw console.error("error performing selector: %o", o), h
                } finally {
                    l && l.removeClass(c)
                }
                return a ? i.uniq(t.map(f, function (t, e) {
                    return a.call(t, e, f, s)
                })) : f
            })
        }, i.matches = function (t, e) {
            return n(e, function (e, n, i) {
                return (!e || o(t, e)) && (!n || n.call(t, null, i) === t)
            })
        }
    }(Zepto)
}, function (t, e, n) {
    n(13), n(10), n(9), n(11), n(12), t.exports = window.Zepto
}, function (t, e) {
    !function (t) {
        function e(e, n, i) {
            var r = t.Event(n);
            return t(e).trigger(r, i), !r.isDefaultPrevented()
        }

        function n(t, n, i, r) {
            return t.global ? e(n || y, i, r) : void 0
        }

        function i(e) {
            e.global && 0 === t.active++ && n(e, null, "ajaxStart")
        }

        function r(e) {
            e.global && !--t.active && n(e, null, "ajaxStop")
        }

        function o(t, e) {
            var i = e.context;
            return e.beforeSend.call(i, t, e) === !1 || n(e, i, "ajaxBeforeSend", [t, e]) === !1 ? !1 : void n(e, i, "ajaxSend", [t, e])
        }

        function a(t, e, i, r) {
            var o = i.context, a = "success";
            i.success.call(o, t, a, e), r && r.resolveWith(o, [t, a, e]), n(i, o, "ajaxSuccess", [e, i, t]), u(a, e, i)
        }

        function s(t, e, i, r, o) {
            var a = r.context;
            r.error.call(a, i, e, t), o && o.rejectWith(a, [i, e, t]), n(r, a, "ajaxError", [i, r, t || e]), u(e, i, r)
        }

        function u(t, e, i) {
            var o = i.context;
            i.complete.call(o, e, t), n(i, o, "ajaxComplete", [e, i]), r(i)
        }

        function c() {
        }

        function l(t) {
            return t && (t = t.split(";", 2)[0]), t && (t == T ? "html" : t == E ? "json" : b.test(t) ? "script" : w.test(t) && "xml") || "text"
        }

        function f(t, e) {
            return "" == e ? t : (t + "&" + e).replace(/[&?]{1,2}/, "?")
        }

        function h(e) {
            e.processData && e.data && "string" != t.type(e.data) && (e.data = t.param(e.data, e.traditional)), !e.data || e.type && "GET" != e.type.toUpperCase() || (e.url = f(e.url, e.data), e.data = void 0)
        }

        function d(e, n, i, r) {
            return t.isFunction(n) && (r = i, i = n, n = void 0), t.isFunction(i) || (r = i, i = void 0), {url: e, data: n, success: i, dataType: r}
        }

        function p(e, n, i, r) {
            var o, a = t.isArray(n), s = t.isPlainObject(n);
            t.each(n, function (n, u) {
                o = t.type(u), r && (n = i ? r : r + "[" + (s || "object" == o || "array" == o ? n : "") + "]"), !r && a ? e.add(u.name, u.value) : "array" == o || !i && "object" == o ? p(e, u, i, n) : e.add(n, u)
            })
        }

        var m, g, v = 0, y = window.document, x = /<script\b[^<]*(?:(?!<\/script>)<[^<]*)*<\/script>/gi, b = /^(?:text|application)\/javascript/i, w = /^(?:text|application)\/xml/i, E = "application/json", T = "text/html", j = /^\s*$/;
        t.active = 0, t.ajaxJSONP = function (e, n) {
            if (!("type" in e))return t.ajax(e);
            var i, r, u = e.jsonpCallback, c = (t.isFunction(u) ? u() : u) || "jsonp" + ++v, l = y.createElement("script"), f = window[c], h = function (e) {
                t(l).triggerHandler("error", e || "abort")
            }, d = {abort: h};
            return n && n.promise(d), t(l).on("load error", function (o, u) {
                clearTimeout(r), t(l).off().remove(), "error" != o.type && i ? a(i[0], d, e, n) : s(null, u || "error", d, e, n), window[c] = f, i && t.isFunction(f) && f(i[0]), f = i = void 0
            }), o(d, e) === !1 ? (h("abort"), d) : (window[c] = function () {
                i = arguments
            }, l.src = e.url.replace(/\?(.+)=\?/, "?$1=" + c), y.head.appendChild(l), e.timeout > 0 && (r = setTimeout(function () {
                h("timeout")
            }, e.timeout)), d)
        }, t.ajaxSettings = {
            type: "GET",
            beforeSend: c,
            success: c,
            error: c,
            complete: c,
            context: null,
            global: !0,
            xhr: function () {
                return new window.XMLHttpRequest
            },
            accepts: {script: "text/javascript, application/javascript, application/x-javascript", json: E, xml: "application/xml, text/xml", html: T, text: "text/plain"},
            crossDomain: !1,
            timeout: 0,
            processData: !0,
            cache: !0
        }, t.ajax = function (e) {
            var n = t.extend({}, e || {}), r = t.Deferred && t.Deferred();
            for (m in t.ajaxSettings)void 0 === n[m] && (n[m] = t.ajaxSettings[m]);
            i(n), n.crossDomain || (n.crossDomain = /^([\w-]+:)?\/\/([^\/]+)/.test(n.url) && RegExp.$2 != window.location.host), n.url || (n.url = window.location.toString()), h(n), n.cache === !1 && (n.url = f(n.url, "_=" + Date.now()));
            var u = n.dataType, d = /\?.+=\?/.test(n.url);
            if ("jsonp" == u || d)return d || (n.url = f(n.url, n.jsonp ? n.jsonp + "=?" : n.jsonp === !1 ? "" : "callback=?")), t.ajaxJSONP(n, r);
            var p, v = n.accepts[u], y = {}, x = function (t, e) {
                y[t.toLowerCase()] = [t, e]
            }, b = /^([\w-]+:)\/\//.test(n.url) ? RegExp.$1 : window.location.protocol, w = n.xhr(), E = w.setRequestHeader;
            if (r && r.promise(w), n.crossDomain || x("X-Requested-With", "XMLHttpRequest"), x("Accept", v || "*/*"), (v = n.mimeType || v) && (v.indexOf(",") > -1 && (v = v.split(",", 2)[0]), w.overrideMimeType && w.overrideMimeType(v)), (n.contentType || n.contentType !== !1 && n.data && "GET" != n.type.toUpperCase()) && x("Content-Type", n.contentType || "application/x-www-form-urlencoded"), n.headers)for (g in n.headers)x(g, n.headers[g]);
            if (w.setRequestHeader = x, w.onreadystatechange = function () {
                    if (4 == w.readyState) {
                        w.onreadystatechange = c, clearTimeout(p);
                        var e, i = !1;
                        if (w.status >= 200 && w.status < 300 || 304 == w.status || 0 == w.status && "file:" == b) {
                            u = u || l(n.mimeType || w.getResponseHeader("content-type")), e = w.responseText;
                            try {
                                "script" == u ? (1, eval)(e) : "xml" == u ? e = w.responseXML : "json" == u && (e = j.test(e) ? null : t.parseJSON(e))
                            } catch (o) {
                                i = o
                            }
                            i ? s(i, "parsererror", w, n, r) : a(e, w, n, r)
                        } else s(w.statusText || null, w.status ? "error" : "abort", w, n, r)
                    }
                }, o(w, n) === !1)return w.abort(), s(null, "abort", w, n, r), w;
            if (n.xhrFields)for (g in n.xhrFields)w[g] = n.xhrFields[g];
            var T = "async" in n ? n.async : !0;
            w.open(n.type, n.url, T, n.username, n.password);
            for (g in y)E.apply(w, y[g]);
            return n.timeout > 0 && (p = setTimeout(function () {
                w.onreadystatechange = c, w.abort(), s(null, "timeout", w, n, r)
            }, n.timeout)), w.send(n.data ? n.data : null), w
        }, t.get = function () {
            return t.ajax(d.apply(null, arguments))
        }, t.post = function () {
            var e = d.apply(null, arguments);
            return e.type = "POST", t.ajax(e)
        }, t.getJSON = function () {
            var e = d.apply(null, arguments);
            return e.dataType = "json", t.ajax(e)
        }, t.fn.load = function (e, n, i) {
            if (!this.length)return this;
            var r, o = this, a = e.split(/\s/), s = d(e, n, i), u = s.success;
            return a.length > 1 && (s.url = a[0], r = a[1]), s.success = function (e) {
                o.html(r ? t("<div>").html(e.replace(x, "")).find(r) : e), u && u.apply(o, arguments)
            }, t.ajax(s), this
        };
        var C = encodeURIComponent;
        t.param = function (t, e) {
            var n = [];
            return n.add = function (t, e) {
                this.push(C(t) + "=" + C(e))
            }, p(n, t, e), n.join("&").replace(/%20/g, "+")
        }
    }(Zepto)
}, function (t, e) {
    !function (t) {
        function e(t) {
            return t._zid || (t._zid = h++)
        }

        function n(t, n, o, a) {
            if (n = i(n), n.ns)var s = r(n.ns);
            return (g[e(t)] || []).filter(function (t) {
                return t && (!n.e || t.e == n.e) && (!n.ns || s.test(t.ns)) && (!o || e(t.fn) === e(o)) && (!a || t.sel == a)
            })
        }

        function i(t) {
            var e = ("" + t).split(".");
            return {e: e[0], ns: e.slice(1).sort().join(" ")}
        }

        function r(t) {
            return new RegExp("(?:^| )" + t.replace(" ", " .* ?") + "(?: |$)")
        }

        function o(t, e) {
            return t.del && !y && t.e in x || !!e
        }

        function a(t) {
            return b[t] || y && x[t] || t
        }

        function s(n, r, s, u, l, h, d) {
            var p = e(n), m = g[p] || (g[p] = []);
            r.split(/\s/).forEach(function (e) {
                if ("ready" == e)return t(document).ready(s);
                var r = i(e);
                r.fn = s, r.sel = l, r.e in b && (s = function (e) {
                    var n = e.relatedTarget;
                    return !n || n !== this && !t.contains(this, n) ? r.fn.apply(this, arguments) : void 0
                }), r.del = h;
                var p = h || s;
                r.proxy = function (t) {
                    if (t = c(t), !t.isImmediatePropagationStopped()) {
                        t.data = u;
                        var e = p.apply(n, t._args == f ? [t] : [t].concat(t._args));
                        return e === !1 && (t.preventDefault(), t.stopPropagation()), e
                    }
                }, r.i = m.length, m.push(r), "addEventListener" in n && n.addEventListener(a(r.e), r.proxy, o(r, d))
            })
        }

        function u(t, i, r, s, u) {
            var c = e(t);
            (i || "").split(/\s/).forEach(function (e) {
                n(t, e, r, s).forEach(function (e) {
                    delete g[c][e.i], "removeEventListener" in t && t.removeEventListener(a(e.e), e.proxy, o(e, u))
                })
            })
        }

        function c(e, n) {
            return (n || !e.isDefaultPrevented) && (n || (n = e), t.each(j, function (t, i) {
                var r = n[t];
                e[t] = function () {
                    return this[i] = w, r && r.apply(n, arguments)
                }, e[i] = E
            }), (n.defaultPrevented !== f ? n.defaultPrevented : "returnValue" in n ? n.returnValue === !1 : n.getPreventDefault && n.getPreventDefault()) && (e.isDefaultPrevented = w)), e
        }

        function l(t) {
            var e, n = {originalEvent: t};
            for (e in t)T.test(e) || t[e] === f || (n[e] = t[e]);
            return c(n, t)
        }

        var f, h = 1, d = Array.prototype.slice, p = t.isFunction, m = function (t) {
            return "string" == typeof t
        }, g = {}, v = {}, y = "onfocusin" in window, x = {focus: "focusin", blur: "focusout"}, b = {mouseenter: "mouseover", mouseleave: "mouseout"};
        v.click = v.mousedown = v.mouseup = v.mousemove = "MouseEvents", t.event = {add: s, remove: u}, t.proxy = function (n, i) {
            if (p(n)) {
                var r = function () {
                    return n.apply(i, arguments)
                };
                return r._zid = e(n), r
            }
            if (m(i))return t.proxy(n[i], n);
            throw new TypeError("expected function")
        }, t.fn.bind = function (t, e, n) {
            return this.on(t, e, n)
        }, t.fn.unbind = function (t, e) {
            return this.off(t, e)
        }, t.fn.one = function (t, e, n, i) {
            return this.on(t, e, n, i, 1)
        };
        var w = function () {
            return !0
        }, E = function () {
            return !1
        }, T = /^([A-Z]|returnValue$|layer[XY]$)/, j = {preventDefault: "isDefaultPrevented", stopImmediatePropagation: "isImmediatePropagationStopped", stopPropagation: "isPropagationStopped"};
        t.fn.delegate = function (t, e, n) {
            return this.on(e, t, n)
        }, t.fn.undelegate = function (t, e, n) {
            return this.off(e, t, n)
        }, t.fn.live = function (e, n) {
            return t(document.body).delegate(this.selector, e, n), this
        }, t.fn.die = function (e, n) {
            return t(document.body).undelegate(this.selector, e, n), this
        }, t.fn.on = function (e, n, i, r, o) {
            var a, c, h = this;
            return e && !m(e) ? (t.each(e, function (t, e) {
                h.on(t, n, i, e, o)
            }), h) : (m(n) || p(r) || r === !1 || (r = i, i = n, n = f), (p(i) || i === !1) && (r = i, i = f), r === !1 && (r = E), h.each(function (f, h) {
                o && (a = function (t) {
                    return u(h, t.type, r), r.apply(this, arguments)
                }), n && (c = function (e) {
                    var i, o = t(e.target).closest(n, h).get(0);
                    return o && o !== h ? (i = t.extend(l(e), {currentTarget: o, liveFired: h}), (a || r).apply(o, [i].concat(d.call(arguments, 1)))) : void 0
                }), s(h, e, r, i, n, c || a)
            }))
        }, t.fn.off = function (e, n, i) {
            var r = this;
            return e && !m(e) ? (t.each(e, function (t, e) {
                r.off(t, n, e)
            }), r) : (m(n) || p(i) || i === !1 || (i = n, n = f), i === !1 && (i = E), r.each(function () {
                u(this, e, i, n)
            }))
        }, t.fn.trigger = function (e, n) {
            return e = m(e) || t.isPlainObject(e) ? t.Event(e) : c(e), e._args = n, this.each(function () {
                "dispatchEvent" in this ? this.dispatchEvent(e) : t(this).triggerHandler(e, n)
            })
        }, t.fn.triggerHandler = function (e, i) {
            var r, o;
            return this.each(function (a, s) {
                r = l(m(e) ? t.Event(e) : e), r._args = i, r.target = s, t.each(n(s, e.type || e), function (t, e) {
                    return o = e.proxy(r), r.isImmediatePropagationStopped() ? !1 : void 0
                })
            }), o
        }, "focusin focusout load resize scroll unload click dblclick mousedown mouseup mousemove mouseover mouseout mouseenter mouseleave change select keydown keypress keyup error".split(" ").forEach(function (e) {
            t.fn[e] = function (t) {
                return t ? this.bind(e, t) : this.trigger(e)
            }
        }), ["focus", "blur"].forEach(function (e) {
            t.fn[e] = function (t) {
                return t ? this.bind(e, t) : this.each(function () {
                    try {
                        this[e]()
                    } catch (t) {
                    }
                }), this
            }
        }), t.Event = function (t, e) {
            m(t) || (e = t, t = e.type);
            var n = document.createEvent(v[t] || "Events"), i = !0;
            if (e)for (var r in e)"bubbles" == r ? i = !!e[r] : n[r] = e[r];
            return n.initEvent(t, i, !0), c(n)
        }
    }(Zepto)
}, function (t, e) {
    !function (t) {
        t.fn.serializeArray = function () {
            var e, n = [];
            return t([].slice.call(this.get(0).elements)).each(function () {
                e = t(this);
                var i = e.attr("type");
                "fieldset" != this.nodeName.toLowerCase() && !this.disabled && "submit" != i && "reset" != i && "button" != i && ("radio" != i && "checkbox" != i || this.checked) && n.push({
                    name: e.attr("name"),
                    value: e.val()
                })
            }), n
        }, t.fn.serialize = function () {
            var t = [];
            return this.serializeArray().forEach(function (e) {
                t.push(encodeURIComponent(e.name) + "=" + encodeURIComponent(e.value))
            }), t.join("&")
        }, t.fn.submit = function (e) {
            if (e)this.bind("submit", e); else if (this.length) {
                var n = t.Event("submit");
                this.eq(0).trigger(n), n.isDefaultPrevented() || this.get(0).submit()
            }
            return this
        }
    }(Zepto)
}, function (t, e) {
    !function (t) {
        "__proto__" in {} || t.extend(t.zepto, {
            Z: function (e, n) {
                return e = e || [], t.extend(e, t.fn), e.selector = n || "", e.__Z = !0, e
            }, isZ: function (e) {
                return "array" === t.type(e) && "__Z" in e
            }
        });
        try {
            getComputedStyle(void 0)
        } catch (e) {
            var n = getComputedStyle;
            window.getComputedStyle = function (t) {
                try {
                    return n(t)
                } catch (e) {
                    return null
                }
            }
        }
    }(Zepto)
}, function (t, e) {
    var n = function () {
        function t(t) {
            return null == t ? String(t) : X[B.call(t)] || "object"
        }

        function e(e) {
            return "function" == t(e)
        }

        function n(t) {
            return null != t && t == t.window
        }

        function i(t) {
            return null != t && t.nodeType == t.DOCUMENT_NODE
        }

        function r(e) {
            return "object" == t(e)
        }

        function o(t) {
            return r(t) && !n(t) && Object.getPrototypeOf(t) == Object.prototype
        }

        function a(t) {
            return "number" == typeof t.length
        }

        function s(t) {
            return P.call(t, function (t) {
                return null != t
            })
        }

        function u(t) {
            return t.length > 0 ? T.fn.concat.apply([], t) : t
        }

        function c(t) {
            return t.replace(/::/g, "/").replace(/([A-Z]+)([A-Z][a-z])/g, "$1_$2").replace(/([a-z\d])([A-Z])/g, "$1_$2").replace(/_/g, "-").toLowerCase()
        }

        function l(t) {
            return t in A ? A[t] : A[t] = new RegExp("(^|\\s)" + t + "(\\s|$)")
        }

        function f(t, e) {
            return "number" != typeof e || Z[c(t)] ? e : e + "px"
        }

        function h(t) {
            var e, n;
            return L[t] || (e = k.createElement(t), k.body.appendChild(e), n = getComputedStyle(e, "").getPropertyValue("display"), e.parentNode.removeChild(e), "none" == n && (n = "block"), L[t] = n), L[t]
        }

        function d(t) {
            return "children" in t ? O.call(t.children) : T.map(t.childNodes, function (t) {
                return 1 == t.nodeType ? t : void 0
            })
        }

        function p(t, e, n) {
            for (E in e)n && (o(e[E]) || G(e[E])) ? (o(e[E]) && !o(t[E]) && (t[E] = {}), G(e[E]) && !G(t[E]) && (t[E] = []), p(t[E], e[E], n)) : e[E] !== w && (t[E] = e[E])
        }

        function m(t, e) {
            return null == e ? T(t) : T(t).filter(e)
        }

        function g(t, n, i, r) {
            return e(n) ? n.call(t, i, r) : n
        }

        function v(t, e, n) {
            null == n ? t.removeAttribute(e) : t.setAttribute(e, n)
        }

        function y(t, e) {
            var n = t.className, i = n && n.baseVal !== w;
            return e === w ? i ? n.baseVal : n : void(i ? n.baseVal = e : t.className = e)
        }

        function x(t) {
            var e;
            try {
                return t ? "true" == t || ("false" == t ? !1 : "null" == t ? null : /^0/.test(t) || isNaN(e = Number(t)) ? /^[\[\{]/.test(t) ? T.parseJSON(t) : t : e) : t
            } catch (n) {
                return t
            }
        }

        function b(t, e) {
            e(t);
            for (var n in t.childNodes)b(t.childNodes[n], e)
        }

        var w, E, T, j, C, S, N = [], O = N.slice, P = N.filter, k = window.document, L = {}, A = {}, Z = {
            "column-count": 1,
            columns: 1,
            "font-weight": 1,
            "line-height": 1,
            opacity: 1,
            "z-index": 1,
            zoom: 1
        }, $ = /^\s*<(\w+|!)[^>]*>/, _ = /^<(\w+)\s*\/?>(?:<\/\1>|)$/, D = /<(?!area|br|col|embed|hr|img|input|link|meta|param)(([\w:]+)[^>]*)\/>/gi, q = /^(?:body|html)$/i, M = /([A-Z])/g, R = ["val", "css", "html", "text", "data", "width", "height", "offset"], z = ["after", "prepend", "before", "append"], F = k.createElement("table"), H = k.createElement("tr"), I = {
            tr: k.createElement("tbody"),
            tbody: F,
            thead: F,
            tfoot: F,
            td: H,
            th: H,
            "*": k.createElement("div")
        }, U = /complete|loaded|interactive/, V = /^[\w-]*$/, X = {}, B = X.toString, J = {}, W = k.createElement("div"), Y = {
            tabindex: "tabIndex",
            readonly: "readOnly",
            "for": "htmlFor",
            "class": "className",
            maxlength: "maxLength",
            cellspacing: "cellSpacing",
            cellpadding: "cellPadding",
            rowspan: "rowSpan",
            colspan: "colSpan",
            usemap: "useMap",
            frameborder: "frameBorder",
            contenteditable: "contentEditable"
        }, G = Array.isArray || function (t) {
                return t instanceof Array
            };
        return J.matches = function (t, e) {
            if (!e || !t || 1 !== t.nodeType)return !1;
            var n = t.webkitMatchesSelector || t.mozMatchesSelector || t.oMatchesSelector || t.matchesSelector;
            if (n)return n.call(t, e);
            var i, r = t.parentNode, o = !r;
            return o && (r = W).appendChild(t), i = ~J.qsa(r, e).indexOf(t), o && W.removeChild(t), i
        }, C = function (t) {
            return t.replace(/-+(.)?/g, function (t, e) {
                return e ? e.toUpperCase() : ""
            })
        }, S = function (t) {
            return P.call(t, function (e, n) {
                return t.indexOf(e) == n
            })
        }, J.fragment = function (t, e, n) {
            var i, r, a;
            return _.test(t) && (i = T(k.createElement(RegExp.$1))), i || (t.replace && (t = t.replace(D, "<$1></$2>")), e === w && (e = $.test(t) && RegExp.$1), e in I || (e = "*"), a = I[e], a.innerHTML = "" + t, i = T.each(O.call(a.childNodes), function () {
                a.removeChild(this)
            })), o(n) && (r = T(i), T.each(n, function (t, e) {
                R.indexOf(t) > -1 ? r[t](e) : r.attr(t, e)
            })), i
        }, J.Z = function (t, e) {
            return t = t || [], t.__proto__ = T.fn, t.selector = e || "", t
        }, J.isZ = function (t) {
            return t instanceof J.Z
        }, J.init = function (t, n) {
            var i;
            if (!t)return J.Z();
            if ("string" == typeof t)if (t = t.trim(), "<" == t[0] && $.test(t))i = J.fragment(t, RegExp.$1, n), t = null; else {
                if (n !== w)return T(n).find(t);
                i = J.qsa(k, t)
            } else {
                if (e(t))return T(k).ready(t);
                if (J.isZ(t))return t;
                if (G(t))i = s(t); else if (r(t))i = [t], t = null; else if ($.test(t))i = J.fragment(t.trim(), RegExp.$1, n), t = null; else {
                    if (n !== w)return T(n).find(t);
                    i = J.qsa(k, t)
                }
            }
            return J.Z(i, t)
        }, T = function (t, e) {
            return J.init(t, e)
        }, T.extend = function (t) {
            var e, n = O.call(arguments, 1);
            return "boolean" == typeof t && (e = t, t = n.shift()), n.forEach(function (n) {
                p(t, n, e)
            }), t
        }, J.qsa = function (t, e) {
            var n, r = "#" == e[0], o = !r && "." == e[0], a = r || o ? e.slice(1) : e, s = V.test(a);
            return i(t) && s && r ? (n = t.getElementById(a)) ? [n] : [] : 1 !== t.nodeType && 9 !== t.nodeType ? [] : O.call(s && !r ? o ? t.getElementsByClassName(a) : t.getElementsByTagName(e) : t.querySelectorAll(e))
        }, T.contains = function (t, e) {
            return t !== e && t.contains(e)
        }, T.type = t, T.isFunction = e, T.isWindow = n, T.isArray = G, T.isPlainObject = o, T.isEmptyObject = function (t) {
            var e;
            for (e in t)return !1;
            return !0
        }, T.inArray = function (t, e, n) {
            return N.indexOf.call(e, t, n)
        }, T.camelCase = C, T.trim = function (t) {
            return null == t ? "" : String.prototype.trim.call(t)
        }, T.uuid = 0, T.support = {}, T.expr = {}, T.map = function (t, e) {
            var n, i, r, o = [];
            if (a(t))for (i = 0; i < t.length; i++)n = e(t[i], i), null != n && o.push(n); else for (r in t)n = e(t[r], r), null != n && o.push(n);
            return u(o)
        }, T.each = function (t, e) {
            var n, i;
            if (a(t)) {
                for (n = 0; n < t.length; n++)if (e.call(t[n], n, t[n]) === !1)return t
            } else for (i in t)if (e.call(t[i], i, t[i]) === !1)return t;
            return t
        }, T.grep = function (t, e) {
            return P.call(t, e)
        }, window.JSON && (T.parseJSON = JSON.parse), T.each("Boolean Number String Function Array Date RegExp Object Error".split(" "), function (t, e) {
            X["[object " + e + "]"] = e.toLowerCase()
        }), T.fn = {
            forEach: N.forEach, reduce: N.reduce, push: N.push, sort: N.sort, indexOf: N.indexOf, concat: N.concat, map: function (t) {
                return T(T.map(this, function (e, n) {
                    return t.call(e, n, e)
                }))
            }, slice: function () {
                return T(O.apply(this, arguments))
            }, ready: function (t) {
                return U.test(k.readyState) && k.body ? t(T) : k.addEventListener("DOMContentLoaded", function () {
                    t(T)
                }, !1), this
            }, get: function (t) {
                return t === w ? O.call(this) : this[t >= 0 ? t : t + this.length]
            }, toArray: function () {
                return this.get()
            }, size: function () {
                return this.length
            }, remove: function () {
                return this.each(function () {
                    null != this.parentNode && this.parentNode.removeChild(this)
                })
            }, each: function (t) {
                return N.every.call(this, function (e, n) {
                    return t.call(e, n, e) !== !1
                }), this
            }, filter: function (t) {
                return e(t) ? this.not(this.not(t)) : T(P.call(this, function (e) {
                    return J.matches(e, t)
                }))
            }, add: function (t, e) {
                return T(S(this.concat(T(t, e))))
            }, is: function (t) {
                return this.length > 0 && J.matches(this[0], t)
            }, not: function (t) {
                var n = [];
                if (e(t) && t.call !== w)this.each(function (e) {
                    t.call(this, e) || n.push(this)
                }); else {
                    var i = "string" == typeof t ? this.filter(t) : a(t) && e(t.item) ? O.call(t) : T(t);
                    this.forEach(function (t) {
                        i.indexOf(t) < 0 && n.push(t)
                    })
                }
                return T(n)
            }, has: function (t) {
                return this.filter(function () {
                    return r(t) ? T.contains(this, t) : T(this).find(t).size()
                })
            }, eq: function (t) {
                return -1 === t ? this.slice(t) : this.slice(t, +t + 1)
            }, first: function () {
                var t = this[0];
                return t && !r(t) ? t : T(t)
            }, last: function () {
                var t = this[this.length - 1];
                return t && !r(t) ? t : T(t)
            }, find: function (t) {
                var e, n = this;
                return e = t ? "object" == typeof t ? T(t).filter(function () {
                    var t = this;
                    return N.some.call(n, function (e) {
                        return T.contains(e, t)
                    })
                }) : 1 == this.length ? T(J.qsa(this[0], t)) : this.map(function () {
                    return J.qsa(this, t)
                }) : []
            }, closest: function (t, e) {
                var n = this[0], r = !1;
                for ("object" == typeof t && (r = T(t)); n && !(r ? r.indexOf(n) >= 0 : J.matches(n, t));)n = n !== e && !i(n) && n.parentNode;
                return T(n)
            }, parents: function (t) {
                for (var e = [], n = this; n.length > 0;)n = T.map(n, function (t) {
                    return (t = t.parentNode) && !i(t) && e.indexOf(t) < 0 ? (e.push(t), t) : void 0
                });
                return m(e, t)
            }, parent: function (t) {
                return m(S(this.pluck("parentNode")), t)
            }, children: function (t) {
                return m(this.map(function () {
                    return d(this)
                }), t)
            }, contents: function () {
                return this.map(function () {
                    return O.call(this.childNodes)
                })
            }, siblings: function (t) {
                return m(this.map(function (t, e) {
                    return P.call(d(e.parentNode), function (t) {
                        return t !== e
                    })
                }), t)
            }, empty: function () {
                return this.each(function () {
                    this.innerHTML = ""
                })
            }, pluck: function (t) {
                return T.map(this, function (e) {
                    return e[t]
                })
            }, show: function () {
                return this.each(function () {
                    "none" == this.style.display && (this.style.display = ""), "none" == getComputedStyle(this, "").getPropertyValue("display") && (this.style.display = h(this.nodeName))
                })
            }, replaceWith: function (t) {
                return this.before(t).remove()
            }, wrap: function (t) {
                var n = e(t);
                if (this[0] && !n)var i = T(t).get(0), r = i.parentNode || this.length > 1;
                return this.each(function (e) {
                    T(this).wrapAll(n ? t.call(this, e) : r ? i.cloneNode(!0) : i)
                })
            }, wrapAll: function (t) {
                if (this[0]) {
                    T(this[0]).before(t = T(t));
                    for (var e; (e = t.children()).length;)t = e.first();
                    T(t).append(this)
                }
                return this
            }, wrapInner: function (t) {
                var n = e(t);
                return this.each(function (e) {
                    var i = T(this), r = i.contents(), o = n ? t.call(this, e) : t;
                    r.length ? r.wrapAll(o) : i.append(o)
                })
            }, unwrap: function () {
                return this.parent().each(function () {
                    T(this).replaceWith(T(this).children())
                }), this
            }, clone: function () {
                return this.map(function () {
                    return this.cloneNode(!0)
                })
            }, hide: function () {
                return this.css("display", "none")
            }, toggle: function (t) {
                return this.each(function () {
                    var e = T(this);
                    (t === w ? "none" == e.css("display") : t) ? e.show() : e.hide()
                })
            }, prev: function (t) {
                return T(this.pluck("previousElementSibling")).filter(t || "*")
            }, next: function (t) {
                return T(this.pluck("nextElementSibling")).filter(t || "*")
            }, html: function (t) {
                return 0 === arguments.length ? this.length > 0 ? this[0].innerHTML : null : this.each(function (e) {
                    var n = this.innerHTML;
                    T(this).empty().append(g(this, t, e, n))
                })
            }, text: function (t) {
                return 0 === arguments.length ? this.length > 0 ? this[0].textContent : null : this.each(function () {
                    this.textContent = t === w ? "" : "" + t
                })
            }, attr: function (t, e) {
                var n;
                return "string" == typeof t && e === w ? 0 == this.length || 1 !== this[0].nodeType ? w : "value" == t && "INPUT" == this[0].nodeName ? this.val() : !(n = this[0].getAttribute(t)) && t in this[0] ? this[0][t] : n : this.each(function (n) {
                    if (1 === this.nodeType)if (r(t))for (E in t)v(this, E, t[E]); else v(this, t, g(this, e, n, this.getAttribute(t)))
                })
            }, removeAttr: function (t) {
                return this.each(function () {
                    1 === this.nodeType && v(this, t)
                })
            }, prop: function (t, e) {
                return t = Y[t] || t, e === w ? this[0] && this[0][t] : this.each(function (n) {
                    this[t] = g(this, e, n, this[t])
                })
            }, data: function (t, e) {
                var n = this.attr("data-" + t.replace(M, "-$1").toLowerCase(), e);
                return null !== n ? x(n) : w
            }, val: function (t) {
                return 0 === arguments.length ? this[0] && (this[0].multiple ? T(this[0]).find("option").filter(function () {
                    return this.selected
                }).pluck("value") : this[0].value) : this.each(function (e) {
                    this.value = g(this, t, e, this.value)
                })
            }, offset: function (t) {
                if (t)return this.each(function (e) {
                    var n = T(this), i = g(this, t, e, n.offset()), r = n.offsetParent().offset(), o = {top: i.top - r.top, left: i.left - r.left};
                    "static" == n.css("position") && (o.position = "relative"), n.css(o)
                });
                if (0 == this.length)return null;
                var e = this[0].getBoundingClientRect();
                return {left: e.left + window.pageXOffset, top: e.top + window.pageYOffset, width: Math.round(e.width), height: Math.round(e.height)}
            }, css: function (e, n) {
                if (arguments.length < 2) {
                    var i = this[0], r = getComputedStyle(i, "");
                    if (!i)return;
                    if ("string" == typeof e)return i.style[C(e)] || r.getPropertyValue(e);
                    if (G(e)) {
                        var o = {};
                        return T.each(G(e) ? e : [e], function (t, e) {
                            o[e] = i.style[C(e)] || r.getPropertyValue(e)
                        }), o
                    }
                }
                var a = "";
                if ("string" == t(e))n || 0 === n ? a = c(e) + ":" + f(e, n) : this.each(function () {
                    this.style.removeProperty(c(e))
                }); else for (E in e)e[E] || 0 === e[E] ? a += c(E) + ":" + f(E, e[E]) + ";" : this.each(function () {
                    this.style.removeProperty(c(E))
                });
                return this.each(function () {
                    this.style.cssText += ";" + a
                })
            }, index: function (t) {
                return t ? this.indexOf(T(t)[0]) : this.parent().children().indexOf(this[0])
            }, hasClass: function (t) {
                return t ? N.some.call(this, function (t) {
                    return this.test(y(t))
                }, l(t)) : !1
            }, addClass: function (t) {
                return t ? this.each(function (e) {
                    j = [];
                    var n = y(this), i = g(this, t, e, n);
                    i.split(/\s+/g).forEach(function (t) {
                        T(this).hasClass(t) || j.push(t)
                    }, this), j.length && y(this, n + (n ? " " : "") + j.join(" "))
                }) : this
            }, removeClass: function (t) {
                return this.each(function (e) {
                    return t === w ? y(this, "") : (j = y(this), g(this, t, e, j).split(/\s+/g).forEach(function (t) {
                        j = j.replace(l(t), " ")
                    }), void y(this, j.trim()))
                })
            }, toggleClass: function (t, e) {
                return t ? this.each(function (n) {
                    var i = T(this), r = g(this, t, n, y(this));
                    r.split(/\s+/g).forEach(function (t) {
                        (e === w ? !i.hasClass(t) : e) ? i.addClass(t) : i.removeClass(t)
                    })
                }) : this
            }, scrollTop: function (t) {
                if (this.length) {
                    var e = "scrollTop" in this[0];
                    return t === w ? e ? this[0].scrollTop : this[0].pageYOffset : this.each(e ? function () {
                        this.scrollTop = t
                    } : function () {
                        this.scrollTo(this.scrollX, t)
                    })
                }
            }, scrollLeft: function (t) {
                if (this.length) {
                    var e = "scrollLeft" in this[0];
                    return t === w ? e ? this[0].scrollLeft : this[0].pageXOffset : this.each(e ? function () {
                        this.scrollLeft = t
                    } : function () {
                        this.scrollTo(t, this.scrollY)
                    })
                }
            }, position: function () {
                if (this.length) {
                    var t = this[0], e = this.offsetParent(), n = this.offset(), i = q.test(e[0].nodeName) ? {
                        top: 0, left: 0
                    } : e.offset();
                    return n.top -= parseFloat(T(t).css("margin-top")) || 0, n.left -= parseFloat(T(t).css("margin-left")) || 0, i.top += parseFloat(T(e[0]).css("border-top-width")) || 0, i.left += parseFloat(T(e[0]).css("border-left-width")) || 0, {
                        top: n.top - i.top,
                        left: n.left - i.left
                    }
                }
            }, offsetParent: function () {
                return this.map(function () {
                    for (var t = this.offsetParent || k.body; t && !q.test(t.nodeName) && "static" == T(t).css("position");)t = t.offsetParent;
                    return t
                })
            }
        }, T.fn.detach = T.fn.remove, ["width", "height"].forEach(function (t) {
            var e = t.replace(/./, function (t) {
                return t[0].toUpperCase()
            });
            T.fn[t] = function (r) {
                var o, a = this[0];
                return r === w ? n(a) ? a["inner" + e] : i(a) ? a.documentElement["scroll" + e] : (o = this.offset()) && o[t] : this.each(function (e) {
                    a = T(this), a.css(t, g(this, r, e, a[t]()))
                })
            }
        }), z.forEach(function (e, n) {
            var i = n % 2;
            T.fn[e] = function () {
                var e, r, o = T.map(arguments, function (n) {
                    return e = t(n), "object" == e || "array" == e || null == n ? n : J.fragment(n)
                }), a = this.length > 1;
                return o.length < 1 ? this : this.each(function (t, e) {
                    r = i ? e : e.parentNode, e = 0 == n ? e.nextSibling : 1 == n ? e.firstChild : 2 == n ? e : null, o.forEach(function (t) {
                        if (a)t = t.cloneNode(!0); else if (!r)return T(t).remove();
                        b(r.insertBefore(t, e), function (t) {
                            null == t.nodeName || "SCRIPT" !== t.nodeName.toUpperCase() || t.type && "text/javascript" !== t.type || t.src || window.eval.call(window, t.innerHTML)
                        })
                    })
                })
            }, T.fn[i ? e + "To" : "insert" + (n ? "Before" : "After")] = function (t) {
                return T(t)[e](this), this
            }
        }), J.Z.prototype = T.fn, J.uniq = S, J.deserializeValue = x, T.zepto = J, T
    }();
    window.Zepto = n, void 0 === window.$ && (window.$ = n), t.exports = n
}]);