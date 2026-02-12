cmake_minimum_required(VERSION 3.18)
project(audio_processor)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# 查找 JNI
find_package(JNI REQUIRED)

# 设置 ONNX Runtime 路径
set(ONNXRUNTIME_DIR ${CMAKE_CURRENT_SOURCE_DIR}/onnxruntime)
set(ONNXRUNTIME_BUILD_DIR ${ONNXRUNTIME_DIR}/build/Windows/Release/Release)

# 检查 ONNX Runtime 是否已编译
if(NOT EXISTS ${ONNXRUNTIME_BUILD_DIR}/onnxruntime.lib)
    message(FATAL_ERROR "ONNX Runtime 未编译！请先按照 BUILD_INSTRUCTIONS.md 中的说明编译 ONNX Runtime。\n"
                        "缺失文件: ${ONNXRUNTIME_BUILD_DIR}/onnxruntime.lib")
endif()

# 包含目录
include_directories(
    ${JNI_INCLUDE_DIRS}
    ${ONNXRUNTIME_DIR}/include/onnxruntime/core/session
    ${ONNXRUNTIME_DIR}/include/onnxruntime/core/providers/cpu
    ${CMAKE_CURRENT_SOURCE_DIR}/pffft
)

# 链接目录
link_directories(${ONNXRUNTIME_BUILD_DIR})

# PFFFT 源文件
set(PFFFT_SOURCES
    pffft/pffft.c
    pffft/pffft_common.c
)

# 创建共享库
add_library(audio_processor SHARED
    audio_processor.cpp
    ${PFFFT_SOURCES}
)

# 链接库
target_link_libraries(audio_processor
    ${JNI_LIBRARIES}
    ${ONNXRUNTIME_BUILD_DIR}/onnxruntime.lib
)

# Windows 特定设置
if(WIN32)
    target_compile_definitions(audio_processor PRIVATE _WINDOWS)
    set_target_properties(audio_processor PROPERTIES
        RUNTIME_OUTPUT_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR}
    )
endif()

# 安装目标
install(TARGETS audio_processor
    RUNTIME DESTINATION ${CMAKE_CURRENT_SOURCE_DIR}/../resources
)

# 复制 ONNX Runtime DLL 到输出目录
add_custom_command(TARGET audio_processor POST_BUILD
    COMMAND ${CMAKE_COMMAND} -E copy_if_different
        ${ONNXRUNTIME_BUILD_DIR}/onnxruntime.dll
        ${CMAKE_CURRENT_SOURCE_DIR}/../resources
    COMMAND ${CMAKE_COMMAND} -E copy_if_different
        ${ONNXRUNTIME_BUILD_DIR}/onnxruntime_providers_shared.dll
        ${CMAKE_CURRENT_SOURCE_DIR}/../resources
    COMMENT "复制 ONNX Runtime DLL 到资源目录"
)

message(STATUS "==========================================")
message(STATUS "audio_processor 配置完成")
message(STATUS "ONNX Runtime 路径: ${ONNXRUNTIME_BUILD_DIR}")
message(STATUS "输出目录: ${CMAKE_CURRENT_SOURCE_DIR}/../resources")
message(STATUS "==========================================")
