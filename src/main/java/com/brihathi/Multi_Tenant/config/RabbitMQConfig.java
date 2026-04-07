// package com.brihathi.Multi_Tenant.config;
 
// import org.springframework.amqp.core.*;
// import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
// import org.springframework.amqp.rabbit.connection.ConnectionFactory;
// import org.springframework.amqp.rabbit.core.RabbitTemplate;
// import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// // import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
// import org.springframework.amqp.support.converter.MessageConverter;

 
// @Configuration
// public class RabbitMQConfig {
 
//     public static final String AI_ANALYSIS_EXCHANGE = "ai_analysis_exchange";
//     public static final String AI_ANALYSIS_ROUTING_KEY = "ai.analysis";
 
//     public static final String EXAM_RESULTS_QUEUE = "exam_results_queue";
//     public static final String CHAPTER_RESULTS_QUEUE = "chapter_results_queue";
//     public static final String SCORE_PREDICTOR_QUEUE = "score_predictor_queue";
//     public static final String TIME_ANALYSIS_QUEUE = "time_analysis_queue";
//     public static final String SUBJECT_WISE_PERFORMANCE_QUEUE = "subject_wise_performance_queue";
//     public static final String SCORE_PROGRESS_QUEUE = "score_progress_queue";
//     public static final String LEADERSHIP_BOARD_QUEUE = "leadership_board_queue";
//     // public static final String ERROR_TRACKER_QUEUE = "error_tracker";
//     // public static final String ERROR_TRACKING_TRENDS_QUEUE = "error_tracking_trends_queue";
 
 
//     // Exchange
//     @Bean
//     public TopicExchange aiAnalysisExchange() {
//         return new TopicExchange(AI_ANALYSIS_EXCHANGE);
//     }
 
//     // Queues
//     @Bean public Queue examResultsQueue() { return new Queue(EXAM_RESULTS_QUEUE); }
//     @Bean public Queue chapterResultsQueue() { return new Queue(CHAPTER_RESULTS_QUEUE); }
//     @Bean public Queue scorePredictorQueue() { return new Queue(SCORE_PREDICTOR_QUEUE); }
//     @Bean public Queue timeAnalysisQueue() { return new Queue(TIME_ANALYSIS_QUEUE); }
//     @Bean public Queue subjectWisePerformanceQueue() { return new Queue(SUBJECT_WISE_PERFORMANCE_QUEUE); }
//  //   @Bean public Queue difficultyWisePerformanceQueue() { return new Queue(DIFFICULTY_WISE_PERFORMANCE_QUEUE); }
//     @Bean public Queue scoreProgressQueue() { return new Queue(SCORE_PROGRESS_QUEUE); }
//     @Bean public Queue leadershipBoardQueue() { return new Queue(LEADERSHIP_BOARD_QUEUE); }
//     // @Bean public Queue errorTrackerQueue() { return new Queue(ERROR_TRACKER_QUEUE); }
//     // @Bean public Queue errorTrackingTrendsQueue() { return new Queue(ERROR_TRACKING_TRENDS_QUEUE);}
 
 
//     // Bindings (all use same routing key)
//     @Bean public Binding bindExamResultsQueue() {
//         return BindingBuilder.bind(examResultsQueue()).to(aiAnalysisExchange()).with(AI_ANALYSIS_ROUTING_KEY);
//     }
 
//     @Bean public Binding bindChapterResultsQueue() {
//         return BindingBuilder.bind(chapterResultsQueue()).to(aiAnalysisExchange()).with(AI_ANALYSIS_ROUTING_KEY);
//     }
 
//     @Bean public Binding bindScorePredictorQueue() {
//         return BindingBuilder.bind(scorePredictorQueue()).to(aiAnalysisExchange()).with(AI_ANALYSIS_ROUTING_KEY);
//     }
 
//     @Bean public Binding bindTimeAnalysisQueue() {
//         return BindingBuilder.bind(timeAnalysisQueue()).to(aiAnalysisExchange()).with(AI_ANALYSIS_ROUTING_KEY);
//     }
 
//     @Bean public Binding bindSubjectWisePerformanceQueue() {
//         return BindingBuilder.bind(subjectWisePerformanceQueue()).to(aiAnalysisExchange()).with(AI_ANALYSIS_ROUTING_KEY);
//     }
 
 
//     @Bean public Binding bindScoreProgressQueue() {
//         return BindingBuilder.bind(scoreProgressQueue()).to(aiAnalysisExchange()).with(AI_ANALYSIS_ROUTING_KEY);
//     }
 
//     @Bean public Binding bindLeadershipBoardQueue() {
//         return BindingBuilder.bind(leadershipBoardQueue()).to(aiAnalysisExchange()).with(AI_ANALYSIS_ROUTING_KEY);
//     }
//     // @Bean public Binding bindErrorTrackerQueue() {
//     //     return BindingBuilder.bind(errorTrackerQueue()).to(aiAnalysisExchangeMains()).with(AI_ANALYSIS_ROUTING_KEY);
//     // }
 
//     // @Bean public Binding bindErrorTrackingTrendsQueue() {
//     //     return BindingBuilder.bind(errorTrackingTrendsQueue()).to(aiAnalysisExchangeMains()).with(AI_ANALYSIS_ROUTING_KEY);
//     // }
 
 
//     // JSON Converter
//     // @Bean
//     // public Jackson2JsonMessageConverter jsonConverter() {
//     //     return new Jackson2JsonMessageConverter();
//     // }
 
//     // RabbitTemplate with JSON converter
//    // RabbitTemplate
// //    @Bean
// //    public RabbitTemplate rabbitTemplate(ConnectionFactory cf, Jackson2JsonMessageConverter converter) {
// //        RabbitTemplate template = new RabbitTemplate(cf);
// //        template.setMessageConverter(converter);
// //        return template;
// //    }
//     // // Listener Container Factory
//     // @Bean
//     // public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
//     //         ConnectionFactory cf,
//     //         Jackson2JsonMessageConverter converter) {
 
//     //     SimpleRabbitListenerContainerFactory factory =
//     //             new SimpleRabbitListenerContainerFactory();
 
//     //     factory.setConnectionFactory(cf);
//     //     factory.setMessageConverter(converter);
//     //     factory.setConcurrentConsumers(3);
//     //     factory.setMaxConcurrentConsumers(10);
//     //     factory.setDefaultRequeueRejected(false);
 
//     //     return factory;
//     // }

    
//     // JSON message converter
//     @Bean
//     public MessageConverter jsonMessageConverter() {
//         return new Jackson2JsonMessageConverter();
//     }
 
//     // RabbitTemplate with JSON converter
//     @Bean
//     public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
//         RabbitTemplate template = new RabbitTemplate(connectionFactory);
//         template.setMessageConverter(jsonMessageConverter());
//         return template;
//     }
// }



package com.brihathi.Multi_Tenant.config;
 
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
 
@Configuration
public class RabbitMQConfig {
 
    public static final String AI_ANALYSIS_EXCHANGE = "ai_analysis_exchange_b2b";
    public static final String AI_ANALYSIS_ROUTING_KEY = "ai.analysis.b2b";
 
    public static final String EXAM_RESULTS_QUEUE = "exam_results_queue_b2b";
    public static final String CHAPTER_RESULTS_QUEUE = "chapter_results_queue_b2b";
    public static final String SCORE_PREDICTOR_QUEUE = "score_predictor_queue_b2b";
    public static final String TIME_ANALYSIS_QUEUE = "time_analysis_queue_b2b";
    public static final String SUBJECT_WISE_PERFORMANCE_QUEUE = "subject_wise_performance_queue_b2b";
    public static final String SCORE_PROGRESS_QUEUE = "score_progress_queue_b2b";
    public static final String LEADERSHIP_BOARD_QUEUE = "leadership_board_queue_b2b";
    public static final String ERROR_TRACKER_QUEUE = "error_tracker_b2b";
    public static final String MYSTERY_BOX_QUEUE = "mystery_box_queue_b2b";
 
 
    /* ================= EXCHANGE ================= */
 
    @Bean
    public TopicExchange aiAnalysisExchange() {
        return new TopicExchange(AI_ANALYSIS_EXCHANGE);
    }
 
    /* ================= QUEUES ================= */
 
    @Bean public Queue examResultsQueue() { return new Queue(EXAM_RESULTS_QUEUE); }
    @Bean public Queue chapterResultsQueue() { return new Queue(CHAPTER_RESULTS_QUEUE); }
    @Bean public Queue scorePredictorQueue() { return new Queue(SCORE_PREDICTOR_QUEUE); }
    @Bean public Queue timeAnalysisQueue() { return new Queue(TIME_ANALYSIS_QUEUE); }
    @Bean public Queue subjectWisePerformanceQueue() { return new Queue(SUBJECT_WISE_PERFORMANCE_QUEUE); }
    @Bean public Queue scoreProgressQueue() { return new Queue(SCORE_PROGRESS_QUEUE); }
    @Bean public Queue leadershipBoardQueue() { return new Queue(LEADERSHIP_BOARD_QUEUE); }
        @Bean public Queue errorTrackerQueue() { return new Queue(ERROR_TRACKER_QUEUE); }
        
 @Bean public Queue mysteryBoxQueue() { return new Queue(MYSTERY_BOX_QUEUE); }
 
    /* ================= BINDINGS (SAME ROUTING KEY) ================= */
 
    @Bean public Binding bindExamResultsQueue() {
        return BindingBuilder.bind(examResultsQueue()).to(aiAnalysisExchange()).with(AI_ANALYSIS_ROUTING_KEY);
    }
 
    @Bean public Binding bindChapterResultsQueue() {
        return BindingBuilder.bind(chapterResultsQueue()).to(aiAnalysisExchange()).with(AI_ANALYSIS_ROUTING_KEY);
    }
 
    @Bean public Binding bindScorePredictorQueue() {
        return BindingBuilder.bind(scorePredictorQueue()).to(aiAnalysisExchange()).with(AI_ANALYSIS_ROUTING_KEY);
    }
 
    @Bean public Binding bindTimeAnalysisQueue() {
        return BindingBuilder.bind(timeAnalysisQueue()).to(aiAnalysisExchange()).with(AI_ANALYSIS_ROUTING_KEY);
    }
 
    @Bean public Binding bindSubjectWisePerformanceQueue() {
        return BindingBuilder.bind(subjectWisePerformanceQueue()).to(aiAnalysisExchange()).with(AI_ANALYSIS_ROUTING_KEY);
    }
 
    @Bean public Binding bindScoreProgressQueue() {
        return BindingBuilder.bind(scoreProgressQueue()).to(aiAnalysisExchange()).with(AI_ANALYSIS_ROUTING_KEY);
    }
 
    @Bean public Binding bindLeadershipBoardQueue() {
        return BindingBuilder.bind(leadershipBoardQueue()).to(aiAnalysisExchange()).with(AI_ANALYSIS_ROUTING_KEY);
    }
       @Bean public Binding bindErrorTrackerQueue() {
        return BindingBuilder.bind(errorTrackerQueue()).to(aiAnalysisExchange()).with(AI_ANALYSIS_ROUTING_KEY);
    }
    
 
@Bean public Binding bindMysteryBoxQueue() {
    return BindingBuilder.bind(mysteryBoxQueue()).to(aiAnalysisExchange()).with(AI_ANALYSIS_ROUTING_KEY);
}

 
    // @Bean public Binding bindErrorTrackingTrendsQueue() {
    //     return BindingBuilder.bind(errorTrackingTrendsQueue()).to(aiAnalysisExchangeMains()).with(AI_ANALYSIS_ROUTING_KEY);
    // }
 
    /* ================= JSON SUPPORT ================= */
 
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
 
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory cf) {
        RabbitTemplate template = new RabbitTemplate(cf);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
 
 
 