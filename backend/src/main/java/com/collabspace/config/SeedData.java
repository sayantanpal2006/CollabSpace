package com.collabspace.config;

import com.collabspace.entity.*;
import com.collabspace.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SeedData {
    @Bean
    CommandLineRunner seed(
            UserRepository users,
            WorkspaceRepository workspaces,
            WorkspaceMemberRepository workspaceMembers,
            ChannelCategoryRepository categories,
            ChannelRepository channels,
            ChannelMemberRepository channelMembers,
            MessageRepository messages,
            PasswordEncoder encoder,
            @Value("${SEED_DATA:false}") boolean enabled
    ) {
        return args -> {
            if (!enabled || users.count() > 0) {
                return;
            }

            User admin = users.save(User.builder()
                    .username("admin")
                    .email("admin@example.com")
                    .password(encoder.encode("Admin@12345"))
                    .role(Role.ADMIN)
                    .build());
            User user = users.save(User.builder()
                    .username("user")
                    .email("user@example.com")
                    .password(encoder.encode("User@12345"))
                    .build());

            Workspace workspace = workspaces.save(Workspace.builder()
                    .name("CollabSpace Demo")
                    .description("Development workspace")
                    .owner(admin)
                    .build());

            workspaceMembers.save(WorkspaceMember.builder().workspace(workspace).user(admin).role(WorkspaceRole.OWNER).build());
            workspaceMembers.save(WorkspaceMember.builder().workspace(workspace).user(user).role(WorkspaceRole.MEMBER).build());

            ChannelCategory planning = new ChannelCategory();
            planning.setWorkspace(workspace);
            planning.setName("Planning");
            planning.setPosition(0);
            categories.save(planning);

            Channel general = channels.save(Channel.builder()
                    .workspace(workspace)
                    .category(planning)
                    .name("general")
                    .description("Company-wide conversation")
                    .createdBy(admin)
                    .build());

            channelMembers.save(ChannelMember.builder().channel(general).user(admin).build());
            channelMembers.save(ChannelMember.builder().channel(general).user(user).build());

            messages.save(Message.builder().channel(general).sender(admin).content("Welcome to CollabSpace 👋").build());
            messages.save(Message.builder().channel(general).sender(user).content("@admin please review today's action items.").build());
        };
    }
}
