-- Flyway migration V1: estrutura inicial do banco Rionautica
-- MySQL 8+
-- Este arquivo cria somente a estrutura; não importa dados existentes.

CREATE TABLE `cliente` (
                           `id` BIGINT NOT NULL AUTO_INCREMENT,
                           `tipo` VARCHAR(2) NOT NULL,
                           `nome` VARCHAR(255) NOT NULL,
                           `email` VARCHAR(255) NOT NULL,
                           `celular` VARCHAR(20) NOT NULL,
                           `endereco` VARCHAR(255) NOT NULL,
                           `numero` VARCHAR(20) NOT NULL,
                           `cep` VARCHAR(8) NOT NULL,
                           `cidade` VARCHAR(255) NOT NULL,
                           `estado` CHAR(2) NOT NULL,
                           PRIMARY KEY (`id`),
                           UNIQUE KEY `uk_cliente_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `cliente_pessoa_fisica` (
                                         `id` BIGINT NOT NULL,
                                         `cpf` VARCHAR(11) NOT NULL,
                                         PRIMARY KEY (`id`),
                                         UNIQUE KEY `uk_cliente_pessoa_fisica_cpf` (`cpf`),
                                         CONSTRAINT `fk_cliente_pessoa_fisica_cliente`
                                             FOREIGN KEY (`id`) REFERENCES `cliente` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `cliente_pessoa_juridica` (
                                           `id` BIGINT NOT NULL,
                                           `cnpj` VARCHAR(14) NOT NULL,
                                           `inscricao_estadual` VARCHAR(50) DEFAULT NULL,
                                           PRIMARY KEY (`id`),
                                           UNIQUE KEY `uk_cliente_pessoa_juridica_cnpj` (`cnpj`),
                                           CONSTRAINT `fk_cliente_pessoa_juridica_cliente`
                                               FOREIGN KEY (`id`) REFERENCES `cliente` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `mecanico` (
                            `id` BIGINT NOT NULL AUTO_INCREMENT,
                            `codigo` VARCHAR(255) NOT NULL,
                            `nome` VARCHAR(255) NOT NULL,
                            PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `produto` (
                           `id` BIGINT NOT NULL AUTO_INCREMENT,
                           `codigo` VARCHAR(255) DEFAULT NULL,
                           `nome` VARCHAR(255) DEFAULT NULL,
                           `descricao` VARCHAR(255) DEFAULT NULL,
                           `marca` VARCHAR(255) DEFAULT NULL,
                           `quantidade` INT DEFAULT NULL,
                           `quantidade_minima` INT DEFAULT NULL,
                           `valor` DOUBLE DEFAULT NULL,
                           PRIMARY KEY (`id`),
                           UNIQUE KEY `uk_produto_codigo` (`codigo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `servico` (
                           `id` BIGINT NOT NULL AUTO_INCREMENT,
                           `codigo` VARCHAR(255) NOT NULL,
                           `nome` VARCHAR(255) NOT NULL,
                           `valor` DOUBLE DEFAULT NULL,
                           `revisao` TINYINT(1) NOT NULL DEFAULT 0,
                           PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `usuario` (
                           `id` BIGINT NOT NULL AUTO_INCREMENT,
                           `login` VARCHAR(255) NOT NULL,
                           `senha` VARCHAR(255) NOT NULL,
                           `nome` VARCHAR(255) NOT NULL,
                           `perfil` VARCHAR(20) NOT NULL,
                           PRIMARY KEY (`id`),
                           UNIQUE KEY `uk_usuario_login` (`login`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `veiculo` (
                           `id` BIGINT NOT NULL AUTO_INCREMENT,
                           `cliente_id` BIGINT NOT NULL,
                           `nome` VARCHAR(255) NOT NULL,
                           `descricao` TEXT,
                           `marca` VARCHAR(255) DEFAULT NULL,
                           `modelo` VARCHAR(255) DEFAULT NULL,
                           `ano` VARCHAR(255) DEFAULT NULL,
                           `chassi` VARCHAR(255) DEFAULT NULL,
                           `horas_atuais` DOUBLE DEFAULT NULL,
                           `intervalo_horas` DOUBLE DEFAULT NULL,
                           `intervalo_meses` INT DEFAULT NULL,
                           `revisao_em_dia` TINYINT(1) DEFAULT NULL,
                           PRIMARY KEY (`id`),
                           KEY `idx_veiculo_cliente` (`cliente_id`),
                           CONSTRAINT `fk_veiculo_cliente`
                               FOREIGN KEY (`cliente_id`) REFERENCES `cliente` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `agendamentos` (
                                `id` BIGINT NOT NULL AUTO_INCREMENT,
                                `cliente_id` BIGINT DEFAULT NULL,
                                `veiculo_id` BIGINT DEFAULT NULL,
                                `descricao` VARCHAR(255) DEFAULT NULL,
                                `status` VARCHAR(255) DEFAULT NULL,
                                `nota_id` BIGINT DEFAULT NULL,
                                PRIMARY KEY (`id`),
                                KEY `idx_agendamentos_cliente` (`cliente_id`),
                                KEY `idx_agendamentos_veiculo` (`veiculo_id`),
                                KEY `idx_agendamentos_nota` (`nota_id`),
                                CONSTRAINT `fk_agendamentos_clientes`
                                    FOREIGN KEY (`cliente_id`) REFERENCES `cliente` (`id`),
                                CONSTRAINT `fk_agendamentos_veiculos`
                                    FOREIGN KEY (`veiculo_id`) REFERENCES `veiculo` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `nota` (
                        `id` BIGINT NOT NULL AUTO_INCREMENT,
                        `data` TIMESTAMP NOT NULL,
                        `cliente_id` BIGINT NOT NULL,
                        `valor_bruto` DOUBLE NOT NULL,
                        `desconto` DOUBLE NOT NULL DEFAULT 0,
                        `valor_total` DOUBLE NOT NULL,
                        `status_pagamento` VARCHAR(10) NOT NULL,
                        `valor_pago` DOUBLE NOT NULL DEFAULT 0,
                        `observacao` TEXT,
                        `IS_ORCAMENTO` TINYINT(1) DEFAULT NULL,
                        `agendamentos_id` BIGINT DEFAULT NULL,
                        PRIMARY KEY (`id`),
                        KEY `idx_nota_cliente` (`cliente_id`),
                        KEY `idx_nota_agendamentos` (`agendamentos_id`),
                        CONSTRAINT `fk_nota_cliente`
                            FOREIGN KEY (`cliente_id`) REFERENCES `cliente` (`id`),
                        CONSTRAINT `fk_nota_agendamentos`
                            FOREIGN KEY (`agendamentos_id`) REFERENCES `agendamentos` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

ALTER TABLE `agendamentos`
    ADD CONSTRAINT `fk_agendamentos_nota`
        FOREIGN KEY (`nota_id`) REFERENCES `nota` (`id`);

CREATE TABLE `pagamento` (
                             `id` BIGINT NOT NULL AUTO_INCREMENT,
                             `nota_id` BIGINT NOT NULL,
                             `valor` DOUBLE NOT NULL,
                             `metodo` VARCHAR(20) NOT NULL,
                             `data` TIMESTAMP NOT NULL,
                             `conta` VARCHAR(255) DEFAULT NULL,
                             PRIMARY KEY (`id`),
                             KEY `idx_pagamento_nota` (`nota_id`),
                             CONSTRAINT `fk_pagamento_nota`
                                 FOREIGN KEY (`nota_id`) REFERENCES `nota` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `produto_nota` (
                                `id` BIGINT NOT NULL AUTO_INCREMENT,
                                `nota_id` BIGINT NOT NULL,
                                `produto_id` BIGINT NOT NULL,
                                `quantidade` INT NOT NULL,
                                `valor_unitario` DOUBLE NOT NULL,
                                `descricao` VARCHAR(255) DEFAULT NULL,
                                PRIMARY KEY (`id`),
                                KEY `idx_produto_nota_nota` (`nota_id`),
                                KEY `idx_produto_nota_produto` (`produto_id`),
                                CONSTRAINT `fk_produto_nota_nota`
                                    FOREIGN KEY (`nota_id`) REFERENCES `nota` (`id`),
                                CONSTRAINT `fk_produto_nota_produto`
                                    FOREIGN KEY (`produto_id`) REFERENCES `produto` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `servico_nota` (
                                `id` BIGINT NOT NULL AUTO_INCREMENT,
                                `nota_id` BIGINT NOT NULL,
                                `servico_id` BIGINT NOT NULL,
                                `quantidade` INT NOT NULL,
                                `valor_unitario` DOUBLE NOT NULL,
                                `descricao` VARCHAR(255) DEFAULT NULL,
                                `mecanico_id` BIGINT DEFAULT NULL,
                                `veiculo_id` BIGINT DEFAULT NULL,
                                `horas_veiculo` DOUBLE DEFAULT NULL,
                                PRIMARY KEY (`id`),
                                KEY `idx_servico_nota_nota` (`nota_id`),
                                KEY `idx_servico_nota_servico` (`servico_id`),
                                KEY `idx_servico_nota_mecanico` (`mecanico_id`),
                                KEY `idx_servico_nota_veiculo` (`veiculo_id`),
                                CONSTRAINT `fk_servico_nota_nota`
                                    FOREIGN KEY (`nota_id`) REFERENCES `nota` (`id`),
                                CONSTRAINT `fk_servico_nota_servico`
                                    FOREIGN KEY (`servico_id`) REFERENCES `servico` (`id`),
                                CONSTRAINT `fk_servico_nota_mecanico`
                                    FOREIGN KEY (`mecanico_id`) REFERENCES `mecanico` (`id`),
                                CONSTRAINT `fk_servico_nota_veiculo`
                                    FOREIGN KEY (`veiculo_id`) REFERENCES `veiculo` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `revisao` (
                           `id` BIGINT NOT NULL AUTO_INCREMENT,
                           `veiculo_id` BIGINT NOT NULL,
                           `nota_id` BIGINT DEFAULT NULL,
                           `mecanico_id` BIGINT DEFAULT NULL,
                           `data_realizacao` DATETIME NOT NULL,
                           `horas_na_revisao` DOUBLE DEFAULT NULL,
                           `proxima_revisao_data` DATETIME DEFAULT NULL,
                           `proxima_revisao_horas` DOUBLE DEFAULT NULL,
                           `observacao` TEXT,
                           PRIMARY KEY (`id`),
                           KEY `idx_revisao_veiculo` (`veiculo_id`),
                           KEY `idx_revisao_nota` (`nota_id`),
                           KEY `idx_revisao_mecanico` (`mecanico_id`),
                           KEY `idx_revisao_data` (`data_realizacao`),
                           CONSTRAINT `fk_revisao_mecanico`
                               FOREIGN KEY (`mecanico_id`) REFERENCES `mecanico` (`id`),
                           CONSTRAINT `fk_revisao_nota`
                               FOREIGN KEY (`nota_id`) REFERENCES `nota` (`id`),
                           CONSTRAINT `fk_revisao_veiculo`
                               FOREIGN KEY (`veiculo_id`) REFERENCES `veiculo` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `servico_veiculo` (
                                   `id` BIGINT NOT NULL AUTO_INCREMENT,
                                   `veiculo_id` BIGINT NOT NULL,
                                   `nota_id` BIGINT DEFAULT NULL,
                                   `servico_id` BIGINT DEFAULT NULL,
                                   `mecanico_id` BIGINT DEFAULT NULL,
                                   `descricao` TEXT,
                                   `data_realizacao` DATETIME NOT NULL,
                                   `horas_na_realizacao` DOUBLE DEFAULT NULL,
                                   PRIMARY KEY (`id`),
                                   KEY `idx_servico_veiculo_veiculo` (`veiculo_id`),
                                   KEY `idx_servico_veiculo_nota` (`nota_id`),
                                   KEY `idx_servico_veiculo_servico` (`servico_id`),
                                   KEY `idx_servico_veiculo_mecanico` (`mecanico_id`),
                                   KEY `idx_servico_veiculo_data` (`data_realizacao`),
                                   CONSTRAINT `fk_servico_veiculo_mecanico`
                                       FOREIGN KEY (`mecanico_id`) REFERENCES `mecanico` (`id`),
                                   CONSTRAINT `fk_servico_veiculo_nota`
                                       FOREIGN KEY (`nota_id`) REFERENCES `nota` (`id`),
                                   CONSTRAINT `fk_servico_veiculo_servico`
                                       FOREIGN KEY (`servico_id`) REFERENCES `servico` (`id`),
                                   CONSTRAINT `fk_servico_veiculo_veiculo`
                                       FOREIGN KEY (`veiculo_id`) REFERENCES `veiculo` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
