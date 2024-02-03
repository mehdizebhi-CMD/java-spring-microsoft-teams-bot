package dev.mehdizebhi.tbot.core;

import com.microsoft.graph.models.*;
import com.microsoft.graph.requests.*;
import org.springframework.lang.NonNull;

import java.util.Optional;

/**
 * Microsoft Teams Graph API Type Safe
 * Graph explore api: https://developer.microsoft.com/en-us/graph/graph-explorer
 */
public interface MicrosoftTeamsGraphApi {

    Optional<Team> createTeam(@NonNull String displayName, String description);

    Optional<TeamCollectionPage> myJoinedTeams();

    Optional<ConversationMemberCollectionPage> membersOfATeam(String teamId);

    Optional<ChannelCollectionPage> channelsOfATeamWhichIAmMemberOf(String teamId);

    Optional<Channel> channelInfo(String teamId, String channelId);

    Optional<Channel> createChannel(String teamId, String displayName, String description);

    Optional<TeamsAppInstallationCollectionPage> appsInATeam(String teamId);

    Optional<TeamsTabCollectionPage> tabsInAChannel(String teamId, String channelId);

    Optional<DriveItemCollectionPage> itemsInATeamDrive(String groupIdForTeams, String items);

    Optional<Chat> createChat(Chat chat);

    Optional<ChatMessage> sendChannelMessage(String content, String teamId, String channelId);

    Optional<ChatMessage> sendChannelMessage(ChatMessage chatMessage, String teamId, String channelId);

    Optional<TeamworkTagCollectionPage> getTagsInATeam(String teamId);

    Optional<TeamworkTag> getASingleTagInATeam(String teamId, String teamworkTagId);

    Optional<TeamworkTag> createATagInATeam(String teamId, TeamworkTag teamworkTag);

    Optional<TeamworkTag> updateATagInATeam(String teamId, String teamworkTagId, TeamworkTag teamworkTag);

    Optional<TeamworkTag> deleteATagInATeam(String teamId, String teamworkTagId);

    Optional<TeamworkTagMemberCollectionPage> getsAllMembersOfATagInATeam(String teamId, String teamworkTagId);

    Optional<TeamworkTagMember> getASingleMemberFromATagInATeam(String teamId, String teamworkTagId, String teamworkTagMemberId);

    Optional<TeamworkTagMember> addsAMemberToATagInATeam(String userId, String teamId, String teamworkTagId);

    Optional<TeamworkTagMember> deleteAMemberFromATagInATeam(String teamId, String teamworkTagId, String teamworkTagMemberId);

    // -----------------------------------------------------
    // Beta Version
    // -----------------------------------------------------

    Optional<ChatMessageCollectionPage> messagesInAChannelWithoutReplies(String groupIdForTeams, String channelId);

    Optional<ChatMessage> messageInAChannel(String groupIdForTeams, String channelId, String messageId);

    Optional<ChatMessageCollectionPage> repliesToAMessageInChannel(String groupIdForTeams, String channelId, String messageId);

    Optional<ChatMessage> replyOfAMessage(String groupIdForTeams, String channelId, String messageId, String replyId);

    Optional<UserScopeTeamsAppInstallationCollectionPage> appsInstalledForUser();

    Optional<ConversationMemberCollectionPage> listMembersOfAChat(String chatId);

    Optional<ConversationMember> memberInAChat(String chatId, String membershipId);
}
