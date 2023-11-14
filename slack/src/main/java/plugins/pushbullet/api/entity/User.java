package plugins.slack.api.entity;

import lombok.Value;

@Value
public class User {
    String iden;
    Double created;
    Double modified;
    String email;
    String emailNormalized;
    String name;
    String imageUrl;
    String maxUploadSize;
    String referredCount;
    String referredIden;
}
