package com.rabbit.foot.algs4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 模拟 Paxos 算法的运行
 *
 * @author LiAo
 * @since 2025-01-02
 */
public class PaxosSimulation {

    public static void main(String[] args) {
        List<Acceptor> acceptors = new ArrayList<>(); // 创建多个 Acceptor
        for (int i = 0; i < 5; i++) {
            acceptors.add(new Acceptor());
        }

        // 创建两个 Proposer，分别发起提案
        Proposer proposer1 = new Proposer(1, "ValueA", acceptors);
        Proposer proposer2 = new Proposer(2, "ValueB", acceptors);
        Proposer proposer3 = new Proposer(3, "ValueC", acceptors);

        proposer1.propose(); // 第一个 Proposer 发起提案
        proposer2.propose(); // 第二个 Proposer 发起提案
        proposer3.propose(); // 第三个 Proposer 发起提案

        // Learner 学习共识结果
        Learner learner = new Learner(acceptors);
        learner.learn();
    }
}

/**
 * 提案类，用于表示一个提案
 */
class Proposal {
    int proposalId; // 提案的唯一编号
    String value;   // 提案的值

    public Proposal(int proposalId, String value) {
        this.proposalId = proposalId;
        this.value = value;
    }

    @Override
    public String toString() {
        return "Proposal{" + "proposalId=" + proposalId + ", value='" + value + '\'' + '}';
    }
}

/**
 * Acceptor 类，负责处理 Prepare 和 Accept 阶段
 */
class Acceptor {
    private int promisedId = -1; // 承诺不接受小于该编号的提案
    private Proposal acceptedProposal = null; // 已接受的提案

    // Prepare 阶段的处理
    public synchronized boolean prepare(int proposalId) {
        // 如果提案编号大于已承诺的编号，则更新承诺并返回 true
        if (proposalId > promisedId) {
            promisedId = proposalId;
            return true;
        }
        return false; // 否则拒绝
    }

    // Accept 阶段的处理
    public synchronized boolean accept(Proposal proposal) {
        // 如果提案编号不小于已承诺的编号，则接受提案并更新
        if (proposal.proposalId >= promisedId) {
            promisedId = proposal.proposalId;
            acceptedProposal = proposal;
            return true;
        }
        return false; // 否则拒绝
    }

    // 获取已接受的提案
    public synchronized Proposal getAcceptedProposal() {
        return acceptedProposal;
    }
}

/**
 * Proposer 类，负责发起提案
 */
class Proposer {
    private int proposalId; // 提案编号
    private String value;   // 提案值
    private final List<Acceptor> acceptors; // 所有的 Acceptor 列表

    public Proposer(int proposalId, String value, List<Acceptor> acceptors) {
        this.proposalId = proposalId;
        this.value = value;
        this.acceptors = acceptors;
    }

    // 发起提案的方法
    public void propose() {
        int prepareResponses = 0; // 记录 Prepare 阶段成功的响应数量
        Proposal highestProposal = null; // 记录最高编号的已接受提案

        // Prepare 阶段
        for (Acceptor acceptor : acceptors) {
            if (acceptor.prepare(proposalId)) { // 请求 Prepare
                prepareResponses++;
                Proposal acceptedProposal = acceptor.getAcceptedProposal();
                // 更新为最高编号的已接受提案
                if (acceptedProposal != null && (highestProposal == null || acceptedProposal.proposalId > highestProposal.proposalId)) {
                    highestProposal = acceptedProposal;
                }
            }
        }

        // 如果超过半数的 Acceptor 接受了 Prepare 请求
        if (prepareResponses > acceptors.size() / 2) {
            // 如果存在已接受的提案，则采用其值
            if (highestProposal != null) {
                value = highestProposal.value;
            }

            Proposal proposal = new Proposal(proposalId, value); // 创建提案
            int acceptResponses = 0; // 记录 Accept 阶段成功的响应数量

            // Accept 阶段
            for (Acceptor acceptor : acceptors) {
                if (acceptor.accept(proposal)) { // 请求 Accept
                    acceptResponses++;
                }
            }

            // 如果超过半数的 Acceptor 接受了提案
            if (acceptResponses > acceptors.size() / 2) {
                System.out.println("Proposal accepted: " + proposal);
            } else {
                System.out.println("Proposal rejected in Accept phase.");
            }
        } else {
            System.out.println("Proposal rejected in Prepare phase.");
        }
    }
}

/**
 * Learner 类，用于学习共识结果
 */
class Learner {
    private final List<Acceptor> acceptors; // 所有的 Acceptor 列表

    public Learner(List<Acceptor> acceptors) {
        this.acceptors = acceptors;
    }

    // 学习共识结果的方法
    public void learn() {
        Map<String, Integer> valueCounts = new HashMap<>(); // 统计每个值的接受次数
        for (Acceptor acceptor : acceptors) {
            Proposal proposal = acceptor.getAcceptedProposal();
            if (proposal != null) {
                // 累计每个值的接受次数
                valueCounts.put(proposal.value, valueCounts.getOrDefault(proposal.value, 0) + 1);
            }
        }

        // 检查是否有值被超过半数的 Acceptor 接受
        for (Map.Entry<String, Integer> entry : valueCounts.entrySet()) {
            if (entry.getValue() > acceptors.size() / 2) {
                System.out.println("Learner: Consensus reached on value: " + entry.getKey());
                return;
            }
        }

        System.out.println("Learner: No consensus reached.");
    }
}
