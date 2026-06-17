package com.kt.ktedu.core.security;

import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.io.CharacterEscapes;
import com.fasterxml.jackson.core.io.SerializedString;
import com.google.common.html.HtmlEscapers;

/* xss 예방 */
public class HtmlCharacterEscapes extends CharacterEscapes {

    private final int[] asciiEscapes;

    public HtmlCharacterEscapes() {
        this.asciiEscapes = CharacterEscapes.standardAsciiEscapesForJSON();

        //  XSS 공격에 쓰이는 핵심 특수문자
        asciiEscapes['<'] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes['>'] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes['&'] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes['\"'] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes['\''] = CharacterEscapes.ESCAPE_CUSTOM;
    }

    @Override
    public int[] getEscapeCodesForAscii() {
        return asciiEscapes;
    }

    @Override
    public SerializableString getEscapeSequence(int ch) {
        // 예: < script > 가 들어오면 &lt;script&gt; 로 자동 치환됩니다.
        String escaped = HtmlEscapers.htmlEscaper().escape(Character.toString((char) ch));
        return new SerializedString(escaped);
    }
}