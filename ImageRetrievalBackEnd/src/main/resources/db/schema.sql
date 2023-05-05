CREATE TABLE IF NOT EXISTS color_moments_table (
    `imageId`   INT         PRIMARY KEY     AUTO_INCREMENT ,
    `clusterId` VARCHAR(50),
    `cmVector`  BLOB,
    `url`       VARCHAR(1000)
);

CREATE TABLE IF NOT EXISTS color_moments_clusters (
     `clusterId`            VARCHAR(50)         PRIMARY KEY,
     `pointCount`           INT,
     `centerCoordinate`     BLOB
);

CREATE TABLE IF NOT EXISTS orb_table (
     `imageId`              VARCHAR(50)         PRIMARY KEY,
     `mat`                  BLOB,
     `url`                  VARCHAR(1000)
);

CREATE TABLE IF NOT EXISTS image_anno_table (
    `pair_id`                    INT             PRIMARY KEY  AUTO_INCREMENT,
    `image_location_1`          VARCHAR(1000),
    `image_location_2`          VARCHAR(1000),
    `image_url_1`               VARCHAR(1000),
    `image_url_2`               VARCHAR(1000),
    `human_anno`                BOOL,
    `color_moments_anno`        BOOL,
    `orb_anno`                  BOOL
);

ALTER TABLE image_anno_table ALTER COLUMN pair_id RESTART WITH 1;