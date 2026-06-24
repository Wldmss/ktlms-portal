package com.kt.ktedu.common.util.network;

import com.jcraft.jsch.*;

import java.io.InputStream;
import java.util.Properties;

public class SftpUtil {

    private SftpUtil() {
        // 인스턴스화 방지
    }

    /**
     * [SFTP 파일 업로드] 원격 보안 서버의 특정 경로로 Input 가공 스트림을 바로 밀어 넣음
     *
     * @param host           서버 IP
     * @param port           포트 (보통 22)
     * @param username       계정명
     * @param password       패스워드
     * @param remoteDirPath  원격지 저장 디렉토리 경로 (예: "/nas/upload/deploy")
     * @param remoteFileName 원격지에 저장될 파일명 (예: "kt_data.txt")
     * @param fileStream     업로드할 파일의 스트림인자
     */
    public static void uploadFile(String host, int port, String username, String password,
                                  String remoteDirPath, String remoteFileName, InputStream fileStream) throws JSchException, SftpException {

        JSch jsch = new JSch();
        Session session = null;
        ChannelSftp channelSftp = null;

        try {
            // 1. 세션 연결 세팅 및 호스트 키 체크 무력화 (사내망 연결용 내부 가이드)
            session = jsch.getSession(username, host, port);
            session.setPassword(password);

            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.setTimeout(5000); // Connect Timeout 5초
            session.connect();

            // 2. SFTP 전용 채널 개방
            Channel channel = session.openChannel("sftp");
            channel.connect();
            channelSftp = (ChannelSftp) channel;

            // 3. 대상 폴더로 이동 (폴더가 없으면 에러가 날 수 있으므로 예외 관리 구역)
            try {
                channelSftp.cd(remoteDirPath);
            } catch (SftpException e) {
                // 폴더가 없으면 자동 생성 후 이동
                channelSftp.mkdir(remoteDirPath);
                channelSftp.cd(remoteDirPath);
            }

            // 4. 단 한 줄로 파일 원격지 스트림 업로드 가동
            channelSftp.put(fileStream, remoteFileName);

        } finally {
            // 5. 서버 커넥션 자원 누수 완전 차단 해제 (필수)
            if (channelSftp != null && channelSftp.isConnected()) {
                channelSftp.disconnect();
            }
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
    }
}