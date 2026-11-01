package com.team2073.eagleforcescoutingapplication.framework.ble;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.json.JSONObject;
import org.junit.Test;
import org.team2073.platformkit.EfPairing;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class BleProtocolTest {

    @Test
    public void framesRespectMtuAndNeverSplitUtf8() throws Exception {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 30; i++) sb.append("café ☃ 🤖 ");
        JSONObject msg = new JSONObject().put("type", "events").put("text", sb.toString());
        List<byte[]> frames = BleProtocol.encode(msg, 23);
        assertTrue(frames.size() > 10);
        for (byte[] f : frames) {
            assertTrue(f.length <= 20);
            try {
                StandardCharsets.UTF_8.newDecoder().decode(ByteBuffer.wrap(f));
            } catch (CharacterCodingException e) {
                throw new AssertionError("frame splits a UTF-8 character");
            }
        }
        BleProtocol.Decoder d = new BleProtocol.Decoder();
        List<JSONObject> out = new ArrayList<>();
        for (byte[] f : frames) out.addAll(d.push(f));
        assertEquals(1, out.size());
        assertEquals(sb.toString(), out.get(0).getString("text"));
    }

    @Test
    public void decoderHandlesMultipleMessagesPerNotification() {
        BleProtocol.Decoder d = new BleProtocol.Decoder();
        List<JSONObject> out = d.push("{\"message\":\"a\"}\n{\"message\":\"b\"}\n{\"mess".getBytes(StandardCharsets.UTF_8));
        assertEquals(2, out.size());
        out = d.push("age\":\"c\"}\n".getBytes(StandardCharsets.UTF_8));
        assertEquals("c", out.get(0).optString("message"));
    }

    @Test
    public void pairingAcceptsEveryStandsFormat() {
        byte[] k = EfPairing.deriveKey("K7QM-4XPA");
        assertEquals(EfPairing.clientProof(k, "aa", "bb", "tab"), EfPairing.clientProof(EfPairing.deriveKey("k7qm 4xpa"), "aa", "bb", "tab"));
        byte[] qr = EfPairing.deriveKey("{\"v\":1,\"t\":\"ef-pair\",\"s\":\"K7QM4XPA\",\"n\":\"stands\"}");
        assertEquals(EfPairing.clientProof(k, "aa", "bb", "tab"), EfPairing.clientProof(qr, "aa", "bb", "tab"));
    }
}
