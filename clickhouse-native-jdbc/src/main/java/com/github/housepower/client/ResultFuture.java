/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.housepower.client;

import com.github.housepower.protocol.Request;
import com.github.housepower.protocol.Response;

import java.util.concurrent.*;

public class ResultFuture implements Future<Response> {

    private final Request request;

    private final CompletableFuture<Response> delegate = new CompletableFuture<>();

    public ResultFuture(Request request) {
        this.request = request;
    }

    public Request getRequest() {
        return request;
    }

    public CompletableFuture<Response> toCompletableFuture() {
        return delegate;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return delegate.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return delegate.isCancelled();
    }

    @Override
    public boolean isDone() {
        return delegate.isDone();
    }

    @Override
    public Response get() throws InterruptedException, ExecutionException {
        return delegate.get();
    }

    @Override
    public Response get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return delegate.get(timeout, unit);
    }
}
