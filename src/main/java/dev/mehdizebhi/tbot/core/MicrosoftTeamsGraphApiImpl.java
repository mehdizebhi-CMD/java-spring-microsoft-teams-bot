package dev.mehdizebhi.tbot.core;

import com.google.gson.JsonPrimitive;
import com.microsoft.graph.models.*;
import com.microsoft.graph.requests.*;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;

import java.time.OffsetDateTime;
import java.util.Optional;

@Slf4j
public class MicrosoftTeamsGraphApiImpl implements MicrosoftTeamsGraphApi {

    final private GraphServiceClient<Request> graphServiceClient;

    public MicrosoftTeamsGraphApiImpl(GraphServiceClient<Request> graphServiceClient) {
        this.graphServiceClient = graphServiceClient;
    }

    @Override
    public Optional<Team> createTeam(String displayName, String description) {
        Team team = new Team();
        team.additionalDataManager().put("template@odata.bind", new JsonPrimitive("https://graph.microsoft.com/v1.0/teamsTemplates('standard')"));
        team.displayName = displayName;
        team.description = description;
        team.createdDateTime = OffsetDateTime.now();

        try {
            var newTeam = graphServiceClient.teams()
                    .buildRequest()
                    .post(team);

            return Optional.of(newTeam);
        } catch (Exception e) {
            log.error("Can not create team: ", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<TeamCollectionPage> myJoinedTeams() {
        try {
            TeamCollectionPage joinedTeams = graphServiceClient.me().joinedTeams()
                    .buildRequest()
                    .get();

            return Optional.of(joinedTeams);
        } catch (Exception e) {
            log.error("Can not get my joined teams: ", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<ConversationMemberCollectionPage> membersOfATeam(String teamId) {
        try {
            ConversationMemberCollectionPage members = graphServiceClient.teams(teamId).members()
                    .buildRequest()
                    .get();

            return Optional.of(members);
        } catch (Exception e) {
            log.error("Can not get members of a team: ", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<ChannelCollectionPage> channelsOfATeamWhichIAmMemberOf(String teamId) {
        try {
            ChannelCollectionPage channels = graphServiceClient.teams(teamId).channels()
                    .buildRequest()
                    .get();

            return Optional.of(channels);
        } catch (Exception e) {
            log.error("Can not get channels of a team which i am member of: ", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Channel> channelInfo(String teamId, String channelId) {
        try {
            Channel channel = graphServiceClient.teams(teamId).channels(channelId)
                    .buildRequest()
                    .get();

            return Optional.of(channel);
        } catch (Exception e) {
            log.error("Can not get channel info: ", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Channel> createChannel(String teamId, String displayName, String description) {
        Channel channel = new Channel();
        channel.displayName = displayName;
        channel.description = description;

        try {
            var newChannel = graphServiceClient.teams(teamId).channels()
                    .buildRequest()
                    .post(channel);

            return Optional.of(newChannel);
        } catch (Exception e) {
            log.error("Can not create channel: ", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<TeamsAppInstallationCollectionPage> appsInATeam(String teamId) {
        try {
            TeamsAppInstallationCollectionPage installedApps = graphServiceClient.teams(teamId).installedApps()
                    .buildRequest()
                    .expand("teamsAppDefinition")
                    .get();

            return Optional.of(installedApps);
        } catch (Exception e) {
            log.error("Can not get apps in a team: ", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<TeamsTabCollectionPage> tabsInAChannel(String teamId, String channelId) {
        try {
            TeamsTabCollectionPage tabs = graphServiceClient.teams(teamId).channels(channelId).tabs()
                    .buildRequest()
                    .expand("teamsApp")
                    .get();

            return Optional.of(tabs);
        } catch (Exception e) {
            log.error("Can not get tabs in a channel: ", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<DriveItemCollectionPage> itemsInATeamDrive(String groupIdForTeams, String items) {
        try {
            DriveItemCollectionPage children = graphServiceClient.groups(groupIdForTeams).drive().items(items).children()
                    .buildRequest()
                    .get();

            return Optional.of(children);
        } catch (Exception e) {
            log.error("Can not get items in a team drive: ", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Chat> createChat(Chat chat) {
        try {
            var newChat = graphServiceClient.chats()
                    .buildRequest()
                    .post(chat);

            return Optional.of(newChat);
        } catch (Exception e) {
            log.error("Can not create chat: ", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<ChatMessage> sendChannelMessage(String content, String teamId, String channelId) {
        ChatMessage chatMessage = new ChatMessage();
        ItemBody body = new ItemBody();
        body.content = content;
        chatMessage.body = body;

        try {
            var newChatMessage = graphServiceClient.teams(teamId).channels(channelId).messages()
                    .buildRequest()
                    .post(chatMessage);

            return Optional.of(newChatMessage);
        } catch (Exception e) {
            log.error("Can not send channel message: ", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<ChatMessage> sendChannelMessage(ChatMessage chatMessage, String teamId, String channelId) {
        try {
            var newChatMessage = graphServiceClient.teams(teamId).channels(channelId).messages()
                    .buildRequest()
                    .post(chatMessage);

            return Optional.of(newChatMessage);
        } catch (Exception e) {
            log.error("Can not send channel message: ", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<TeamworkTagCollectionPage> getTagsInATeam(String teamId) {
        try {
            TeamworkTagCollectionPage tags = graphServiceClient.teams(teamId).tags()
                    .buildRequest()
                    .get();

            return Optional.of(tags);
        } catch (Exception e) {
            log.error("Can not get tags in a team: ", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<TeamworkTag> getASingleTagInATeam(String teamId, String teamworkTagId) {
        try {
            TeamworkTag teamworkTag = graphServiceClient.teams(teamId).tags(teamworkTagId)
                    .buildRequest()
                    .get();

            return Optional.of(teamworkTag);
        } catch (Exception e) {
            log.error("Can not get a single tag in a team: ", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<TeamworkTag> createATagInATeam(String teamId, TeamworkTag teamworkTag) {
        try {
            var newTeamworkTag = graphServiceClient.teams(teamId).tags()
                    .buildRequest()
                    .post(teamworkTag);

            return Optional.of(newTeamworkTag);
        } catch (Exception e) {
            log.error("Can not create a tag in a team: ", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<TeamworkTag> updateATagInATeam(String teamId, String teamworkTagId, TeamworkTag teamworkTag) {
        try {
            var newTeamworkTag = graphServiceClient.teams(teamId).tags(teamworkTagId)
                    .buildRequest()
                    .patch(teamworkTag);

            return Optional.of(newTeamworkTag);
        } catch (Exception e) {
            log.error("Can not update a tag in a team: ", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<TeamworkTag> deleteATagInATeam(String teamId, String teamworkTagId) {
        try {
            var deleteTeamworkTag = graphServiceClient.teams(teamId).tags(teamworkTagId)
                    .buildRequest()
                    .delete();

            return Optional.of(deleteTeamworkTag);
        } catch (Exception e) {
            log.error("Can not delete a tag in a team: ", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<TeamworkTagMemberCollectionPage> getsAllMembersOfATagInATeam(String teamId, String teamworkTagId) {
        try {
            TeamworkTagMemberCollectionPage members = graphServiceClient.teams(teamId).tags(teamworkTagId).members()
                    .buildRequest()
                    .get();

            return Optional.of(members);
        } catch (Exception e) {
            log.error("Can not get all members of a tag in a team: ", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<TeamworkTagMember> getASingleMemberFromATagInATeam(String teamId, String teamworkTagId, String teamworkTagMemberId) {
        try {
            TeamworkTagMember teamworkTagMember = graphServiceClient.teams(teamId).tags(teamworkTagId).members(teamworkTagMemberId)
                    .buildRequest()
                    .get();

            return Optional.of(teamworkTagMember);
        } catch (Exception e) {
            log.error("Can not get a single member from a tag in a team: ", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<TeamworkTagMember> addsAMemberToATagInATeam(String userId, String teamId, String teamworkTagId) {
        TeamworkTagMember teamworkTagMember = new TeamworkTagMember();
        teamworkTagMember.userId = userId;

        try {
            var newTeamworkTagMember = graphServiceClient.teams(teamId).tags(teamworkTagId).members()
                    .buildRequest()
                    .post(teamworkTagMember);

            return Optional.of(newTeamworkTagMember);
        } catch (Exception e) {
            log.error("Can not adds a member to a tag in a team: ", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<TeamworkTagMember> deleteAMemberFromATagInATeam(String teamId, String teamworkTagId, String teamworkTagMemberId) {
        try {
            var deleteTeamworkTagMember = graphServiceClient.teams(teamId).tags(teamworkTagId).members(teamworkTagMemberId)
                    .buildRequest()
                    .delete();

            return Optional.of(deleteTeamworkTagMember);
        } catch (Exception e) {
            log.error("Can not delete a member from a tag in a team: ", e);
            return Optional.empty();
        }
    }

    // -----------------------------------------------------
    // Beta Version
    // -----------------------------------------------------

    @Override
    public Optional<ChatMessageCollectionPage> messagesInAChannelWithoutReplies(String groupIdForTeams, String channelId) {
        try {
            ChatMessageCollectionPage messages = graphServiceClient.teams(groupIdForTeams).channels(channelId).messages()
                    .buildRequest()
                    .get();

            return Optional.of(messages);
        } catch (Exception e) {
            log.error("Can not get messages in a channel without replies: ", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<ChatMessage> messageInAChannel(String groupIdForTeams, String channelId, String messageId) {
        try {
            ChatMessage chatMessage = graphServiceClient.teams(groupIdForTeams).channels(channelId).messages(messageId)
                    .buildRequest()
                    .get();

            return Optional.of(chatMessage);
        } catch (Exception e) {
            log.error("Can not get messages in a channel: ", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<ChatMessageCollectionPage> repliesToAMessageInChannel(String groupIdForTeams, String channelId, String messageId) {
        try {
            ChatMessageCollectionPage replies = graphServiceClient.teams(groupIdForTeams).channels(channelId).messages(messageId).replies()
                    .buildRequest()
                    .get();

            return Optional.of(replies);
        } catch (Exception e) {
            log.error("Can not get replies to a message in channel: ", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<ChatMessage> replyOfAMessage(String groupIdForTeams, String channelId, String messageId, String replyId) {
        try {
            ChatMessage chatMessage = graphServiceClient.teams(groupIdForTeams).channels(channelId).messages(messageId).replies(replyId)
                    .buildRequest()
                    .get();

            return Optional.of(chatMessage);
        } catch (Exception e) {
            log.error("Can not get reply of a message: ", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<UserScopeTeamsAppInstallationCollectionPage> appsInstalledForUser() {
        try {
            UserScopeTeamsAppInstallationCollectionPage installedApps = graphServiceClient.me().teamwork().installedApps()
                    .buildRequest()
                    .expand("teamsApp")
                    .get();

            return Optional.of(installedApps);
        } catch (Exception e) {
            log.error("Can not get apps installed for user: ", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<ConversationMemberCollectionPage> listMembersOfAChat(String chatId) {
        try {
            ConversationMemberCollectionPage members = graphServiceClient.chats(chatId).members()
                    .buildRequest()
                    .get();

            return Optional.of(members);
        } catch (Exception e) {
            log.error("Can not get list of members of a chat: ", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<ConversationMember> memberInAChat(String chatId, String membershipId) {
        try {
            ConversationMember conversationMember = graphServiceClient.chats(chatId).members(membershipId)
                    .buildRequest()
                    .get();

            return Optional.of(conversationMember);
        } catch (Exception e) {
            log.error("Can not get member in a chat: ", e);
            return Optional.empty();
        }
    }
}
