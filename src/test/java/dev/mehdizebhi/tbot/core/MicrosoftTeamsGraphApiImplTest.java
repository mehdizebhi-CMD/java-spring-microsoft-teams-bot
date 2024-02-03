package dev.mehdizebhi.tbot.core;

import com.microsoft.graph.logger.DefaultLogger;
import com.microsoft.graph.serializer.DefaultSerializer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class MicrosoftTeamsGraphApiImplTest {

    private @Autowired MicrosoftTeamsGraphApi teamsGraphApi;
    public DefaultSerializer serializer;
    private final static String TEAM_ID = "";
    private final static String CHANNEL_ID = "";

    @BeforeEach
    public void setUp() {
        serializer = new DefaultSerializer(new DefaultLogger());
    }

    @Test
    void createTeam() {
    }

    @Test
    void myJoinedTeams() {
    }

    @Test
    void membersOfATeam() {
    }

    @Test
    void channelsOfATeamWhichIAmMemberOf() {
    }

    @Test
    void channelInfo() {
    }

    @Test
    void createChannel() {
    }

    @Test
    void appsInATeam() {
    }

    @Test
    void tabsInAChannel() {
    }

    @Test
    void itemsInATeamDrive() {
    }

    @Test
    void createChat() {
    }

    @Test
    void sendChannelMessage() {
        assertTrue(teamsGraphApi.sendChannelMessage("This is a test message from FinitX notification bot. please ignore it.", TEAM_ID, CHANNEL_ID).isPresent());
    }

    @Test
    void getTagsInATeam() {
    }

    @Test
    void getASingleTagInATeam() {
    }

    @Test
    void createATagInATeam() {
    }

    @Test
    void updateATagInATeam() {
    }

    @Test
    void deleteATagInATeam() {
    }

    @Test
    void getsAllMembersOfATagInATeam() {
    }

    @Test
    void getASingleMemberFromATagInATeam() {
    }

    @Test
    void addsAMemberToATagInATeam() {
    }

    @Test
    void deleteAMemberFromATagInATeam() {
    }

    @Test
    void messagesInAChannelWithoutReplies() {
        assertTrue(teamsGraphApi.messagesInAChannelWithoutReplies(TEAM_ID, CHANNEL_ID).isPresent());
    }

    @Test
    void messageInAChannel() {
    }

    @Test
    void repliesToAMessageInChannel() {
    }

    @Test
    void replyOfAMessage() {
    }

    @Test
    void appsInstalledForUser() {
    }

    @Test
    void listMembersOfAChat() {
    }

    @Test
    void memberInAChat() {
    }
}