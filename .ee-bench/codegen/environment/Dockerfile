FROM eclipse-temurin:8

ENV DEBIAN_FRONTEND=noninteractive

RUN apt-get update && \
    apt-get install -y \
    build-essential \
    git \
    curl \
    wget \
    maven \
    python3 \
    python3-pip \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /repo
RUN git init && \
    git remote add origin https://github.com/dpaia/train-ticket.git && \
    git fetch origin --depth 1 {{ instance.base_commit }} && \
    git reset --hard FETCH_HEAD

# Pre-fetch dependencies into the image so validation can run offline from cache.
RUN mvn dependency:go-offline -q

LABEL ee-bench.type="codegen"
LABEL ee-bench.version="1.0"
RUN rm -rf /repo/.ee-bench/ 2>/dev/null || true
