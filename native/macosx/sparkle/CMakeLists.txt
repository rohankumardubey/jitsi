project(sparkle_init OBJC)

set(CMAKE_PREFIX_PATH ${PROJECT_SOURCE_DIR}/../../../resources/install/src/mac/dist/Jitsi.app/Contents/Frameworks)

find_package(JNI REQUIRED)
find_package(Sparkle REQUIRED)

add_library(${PROJECT_NAME} SHARED
            net_java_sip_communicator_impl_sparkle_SparkleActivator.m
            )

target_include_directories(${PROJECT_NAME} PUBLIC
                           ${JNI_INCLUDE_DIRS}
                           ${CMAKE_CURRENT_LIST_DIR}/../../../modules/impl/sparkle/target/native/javah
                           ${SPARKLE_INCLUDE_DIR}
                           )

target_link_libraries(${PROJECT_NAME}
                      "-framework AppKit"
                      "-framework Foundation"
                      ${SPARKLE_LIBRARY}
                      )
