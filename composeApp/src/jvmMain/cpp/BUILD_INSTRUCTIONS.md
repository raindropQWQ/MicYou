# 编译说明

本文档说明如何编译 `audio_processor.dll` 和 `onnxruntime.dll`。

## 快速开始（推荐）

如果你不想从源代码编译，可以直接下载预编译的 ONNX Runtime：

### Windows

1. 访问 https://github.com/microsoft/onnxruntime/releases
2. 下载 `onnxruntime-win-x64-1.14.1.zip`
3. 解压后将以下文件复制到 `composeApp/src/jvmMain/resources/`：
   - `onnxruntime.dll`
   - `onnxruntime_providers_shared.dll`

### Linux

```bash
# 使用包管理器安装
sudo apt-get install libonnxruntime-dev

# 或下载预编译版本
wget https://github.com/microsoft/onnxruntime/releases/download/v1.14.1/onnxruntime-linux-x64-1.14.1.tgz
tar -xzf onnxruntime-linux-x64-1.14.1.tgz
cp onnxruntime-linux-x64-1.14.1/lib/libonnxruntime.so* composeApp/src/jvmMain/resources/
```

### macOS

```bash
# 使用 Homebrew 安装
brew install onnxruntime

# 或下载预编译版本
wget https://github.com/microsoft/onnxruntime/releases/download/v1.14.1/onnxruntime-osx-x64-1.14.1.tgz
tar -xzf onnxruntime-osx-x64-1.14.1.tgz
cp onnxruntime-osx-x64-1.14.1/lib/libonnxruntime.*.dylib composeApp/src/jvmMain/resources/
```

## 目录结构

```
cpp/
├── audio_processor.cpp    # 音频处理器源代码
├── audio_processor.dll    # 编译后的音频处理器 DLL（需要自行编译）
├── onnxruntime/           # ONNX Runtime 源代码子模块
├── pffft/                 # PFFFT 源代码子模块
└── BUILD_INSTRUCTIONS.md  # 本文档
```

## 前提条件

### Windows

1. **Visual Studio 2019 或更高版本**（需要安装 C++ 桌面开发工作负载）
2. **CMake 3.18 或更高版本**
3. **Git**

### Linux

1. **GCC 9+ 或 Clang 10+**
2. **CMake 3.18 或更高版本**
3. **Git**
4. **OpenMP**（可选，用于性能优化）

```bash
# Ubuntu/Debian
sudo apt-get update
sudo apt-get install -y build-essential cmake git libopenmp-dev

# CentOS/RHEL/Fedora
sudo yum install -y gcc gcc-c++ cmake git
```

### macOS

1. **Xcode Command Line Tools**
2. **CMake 3.18 或更高版本**
3. **Git**

```bash
# 安装 Xcode Command Line Tools
xcode-select --install

# 使用 Homebrew 安装其他依赖
brew install cmake git
```

## 步骤 1：初始化子模块

```bash
cd <项目根目录>
git submodule update --init --recursive
```

## 步骤 2：编译 ONNX Runtime

ONNX Runtime 提供了官方编译脚本，使用脚本编译更加简单可靠。

### Windows

#### 2.1 打开 Visual Studio 命令提示符

搜索并打开 **"x64 Native Tools Command Prompt for VS 2019"**（或更高版本）

#### 2.2 使用官方脚本编译

```bash
cd composeApp\src\jvmMain\cpp\onnxruntime

# 使用官方 build.bat 脚本编译
.\build.bat --config Release --build_shared_lib --parallel --skip_tests
```

#### 2.3 获取 DLL 文件

编译完成后，DLL 文件位于：
- `build\Windows\Release\Release\onnxruntime.dll`
- `build\Windows\Release\Release\onnxruntime_providers_shared.dll`

### Linux

```bash
cd composeApp/src/jvmMain/cpp/onnxruntime

# 使用官方 build.sh 脚本编译
./build.sh --config Release --build_shared_lib --parallel --skip_tests
```

#### 获取 SO 文件

编译完成后，库文件位于：
- `build/Linux/Release/libonnxruntime.so`
- `build/Linux/Release/libonnxruntime_providers_shared.so`

### macOS

```bash
cd composeApp/src/jvmMain/cpp/onnxruntime

# 使用官方 build.sh 脚本编译（自动识别 macOS 平台）
./build.sh --config Release --build_shared_lib --parallel --skip_tests
```

#### 获取 Dylib 文件

编译完成后，库文件位于：
- `build/macOS/Release/libonnxruntime.dylib`
- `build/macOS/Release/libonnxruntime_providers_shared.dylib`

## 步骤 3：编译 audio_processor

### Windows

```bash
cd composeApp\src\jvmMain\cpp

# 创建构建目录
mkdir build
cd build

# 运行 CMake
cmake .. -G "Visual Studio 16 2019" -A x64

# 编译
cmake --build . --config Release

# DLL 将生成在 Release\audio_processor.dll
```

### Linux

```bash
cd composeApp/src/jvmMain/cpp

# 创建构建目录
mkdir -p build
cd build

# 运行 CMake
cmake .. \
  -DCMAKE_BUILD_TYPE=Release \
  -DJAVA_HOME=$JAVA_HOME

# 编译
cmake --build . --config Release --parallel $(nproc)

# SO 文件将生成在 libaudio_processor.so
```

### macOS

```bash
cd composeApp/src/jvmMain/cpp

# 创建构建目录
mkdir -p build
cd build

# 运行 CMake
cmake .. \
  -DCMAKE_BUILD_TYPE=Release \
  -DJAVA_HOME=$JAVA_HOME

# 编译
cmake --build . --config Release --parallel $(sysctl -n hw.ncpu)

# Dylib 文件将生成在 libaudio_processor.dylib
```

## 步骤 4：复制库文件到资源目录

### Windows

```bash
# 从 ONNX Runtime 构建目录
copy composeApp\src\jvmMain\cpp\onnxruntime\build\Windows\Release\Release\onnxruntime.dll composeApp\src\jvmMain\resources\
copy composeApp\src\jvmMain\cpp\onnxruntime\build\Windows\Release\Release\onnxruntime_providers_shared.dll composeApp\src\jvmMain\resources\

# 从 audio_processor 构建目录
copy composeApp\src\jvmMain\cpp\build\Release\audio_processor.dll composeApp\src\jvmMain\resources\
```

### Linux

```bash
# 从 ONNX Runtime 构建目录
cp composeApp/src/jvmMain/cpp/onnxruntime/build/Linux/Release/libonnxruntime.so composeApp/src/jvmMain/resources/
cp composeApp/src/jvmMain/cpp/onnxruntime/build/Linux/Release/libonnxruntime_providers_shared.so composeApp/src/jvmMain/resources/

# 从 audio_processor 构建目录
cp composeApp/src/jvmMain/cpp/build/libaudio_processor.so composeApp/src/jvmMain/resources/
```

### macOS

```bash
# 从 ONNX Runtime 构建目录
cp composeApp/src/jvmMain/cpp/onnxruntime/build/macOS/Release/libonnxruntime.dylib composeApp/src/jvmMain/resources/
cp composeApp/src/jvmMain/cpp/onnxruntime/build/macOS/Release/libonnxruntime_providers_shared.dylib composeApp/src/jvmMain/resources/

# 从 audio_processor 构建目录
cp composeApp/src/jvmMain/cpp/build/libaudio_processor.dylib composeApp/src/jvmMain/resources/
```

## 验证编译结果

编译完成后，检查资源目录中是否包含以下文件：

### Windows
- `onnxruntime.dll`
- `onnxruntime_providers_shared.dll`
- `audio_processor.dll`

### Linux
- `libonnxruntime.so`
- `libonnxruntime_providers_shared.so`
- `libaudio_processor.so`

### macOS
- `libonnxruntime.dylib`
- `libonnxruntime_providers_shared.dylib`
- `libaudio_processor.dylib`

## 参考资料

- [ONNX Runtime 构建文档](https://onnxruntime.ai/docs/build/)
- [ONNX Runtime GitHub](https://github.com/microsoft/onnxruntime)
- [PFFFT GitHub](https://github.com/marton78/pffft)
