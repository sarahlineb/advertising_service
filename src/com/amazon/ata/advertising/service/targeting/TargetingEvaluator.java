package com.amazon.ata.advertising.service.targeting;

import com.amazon.ata.advertising.service.model.RequestContext;
import com.amazon.ata.advertising.service.targeting.predicate.TargetingPredicate;
import com.amazon.ata.advertising.service.targeting.predicate.TargetingPredicateResult;

import java.lang.annotation.Target;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Stream;

/**
 * Evaluates TargetingPredicates for a given RequestContext.
 */
public class TargetingEvaluator {
    public static final boolean IMPLEMENTED_STREAMS = true;
    public static final boolean IMPLEMENTED_CONCURRENCY = true;
    private final RequestContext requestContext;
    private boolean allTruePredicates;

    /**
     * Creates an evaluator for targeting predicates.
     *
     * @param requestContext Context that can be used to evaluate the predicates.
     */
    public TargetingEvaluator(RequestContext requestContext) {
        this.requestContext = requestContext;
    }

    /**
     * Evaluate a TargetingGroup to determine if all of its TargetingPredicates are TRUE or not for the given
     * RequestContext.
     *
     * @param targetingGroup Targeting group for an advertisement, including TargetingPredicates.
     * @return TRUE if all of the TargetingPredicates evaluate to TRUE against the RequestContext, FALSE otherwise.
     */

    //compare ALL TargetingPredicates against RequestContext for TRUE, or else FALSE
    //
    //sort what items can be returned
    public TargetingPredicateResult evaluate(TargetingGroup targetingGroup) {

        ExecutorService executorService = Executors.newCachedThreadPool();

        try{
            return executorService.submit(() -> (targetingGroup.getTargetingPredicates().parallelStream())
                    .allMatch(targetingPredicate -> targetingPredicate.evaluate(requestContext).isTrue())
                    ? TargetingPredicateResult.TRUE :TargetingPredicateResult.FALSE).get();
        } catch (CancellationException e) {

        }catch (ExecutionException e) {

        }catch (InterruptedException e) {

        }
        executorService.shutdown();
        return TargetingPredicateResult.FALSE;
    }
}


//
//        long count = targetingGroup.getTargetingPredicates().stream()
//                .map(p -> executor.submit(() -> p.evaluate(requestContext)))
//                .filter(p -> {
//                    try {
//                        return !p.get().isTrue();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    } catch (ExecutionException e) {
//                        e.printStackTrace();
//                    }
//                    return false;
//                })
//                .count();
//
//        return count > 0 ? TargetingPredicateResult.FALSE :
//                TargetingPredicateResult.TRUE;
//
//        List<TargetingPredicate> targetingPredicates = targetingGroup.getTargetingPredicates();
//        Stream<TargetingPredicate> targetingPredicateStream = targetingPredicates.stream();
//
//        String stringResult = targetingGroup.getTargetingGroupId();
//        Future<String> result = executor.submit(() -> );


//MT3 code
//        targetingPredicateStream -> {
//            executor.submit();
//        });
//
//        return allTruePredicates ? TargetingPredicateResult.TRUE :
//                                    TargetingPredicateResult.FALSE;
//    }
//        private Runnable evalPredicate (TargetingPredicate predicate) {
//            TargetingPredicateResult predicateResult = predicate.evaluate(requestContext);
//            if(!predicateResult.isTrue()) {
//                strikeFalsePredicates();
//            }
//            return null;
//        }
//
//        private void strikeFalsePredicates() {
//            allTruePredicates = false;
//        }



//MT1 code
//        return targetingGroup.getTargetingPredicates()
//                .stream()
//                .allMatch(targetingPredicate  -> targetingPredicate.evaluate(requestContext).isTrue())
//                ? TargetingPredicateResult.TRUE :                 TargetingPredicateResult.FALSE;


//Original code here
//        List<TargetingPredicate> targetingPredicates = targetingGroup.getTargetingPredicates();
//        boolean allTruePredicates = true;
//        for (TargetingPredicate predicate : targetingPredicates) {
//            TargetingPredicateResult predicateResult = predicate.evaluate(requestContext);
//            if (!predicateResult.isTrue()) {
//                allTruePredicates = false;
//                break;
//            }
//        }
//
//        return allTruePredicates ? TargetingPredicateResult.TRUE :
//                                   TargetingPredicateResult.FALSE;


