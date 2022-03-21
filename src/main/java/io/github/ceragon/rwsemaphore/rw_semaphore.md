# rw semaphore

## 单类型

### read1

| 时刻         | count 状态            | 备注  |
|------------|---------------------|-----|
| r1 lock    | 0000_0000_0000_0001 |     |
| r1 unlock  | 0000_0000_0000_0000 |     |

### read1 + read2

| 时刻        | count 状态            | 备注  |
|-----------|---------------------|-----|
| r1 lock   | 0000_0000_0000_0001 |     |
| r2 lock   | 0000_0000_0000_0002 |     |
| r1 unlock | 0000_0000_0000_0001 |     |
| r2 unlock | 0000_0000_0000_0000 |     |

### write1

| 时刻        | count 状态             | 备注  |
|-----------|----------------------|-----|
| w1 lock   | FFFF_FFFF_0000_0001  |     |
| w1 unlock | 0000_0000_0000_0000  |     |

### write1 + write2

#### 情形一

| 时刻           | count 状态             | 备注      |
|--------------|----------------------|---------|
| w1 lock      | FFFF_FFFF_0000_0001  |         |
| w2 lock      | FFFF_FFFE_0000_0002  |         |
| w2 wait_list | FFFF_FFFE_0000_0001  | 进入等待队列后 |
| w1 unlock    | FFFF_FFFF_0000_0000  |         |
| w1 do_wake   | FFFF_FFFF_0000_0001  |         |
| w2 unlock    | 0000_0000_0000_0000  |         |

#### 情形二

| 时刻            | count 状态            | 备注      |
|---------------|---------------------|---------|
| w1 lock       | FFFF_FFFF_0000_0001 |         |
| w2 lock       | FFFF_FFFE_0000_0002 |         |
| w1 unlock     | FFFF_FFFF_0000_0001 |         |
| w2 wait_list  | FFFF_FFFF_0000_0000 | 进入等待队列后 |
| w2 do_wake    | FFFF_FFFF_0000_0001 |         |
| w2 unlock     | 0000_0000_0000_0000 |         |

### write1 + write2 + write3

#### 情形一

| 时刻           | count 状态             | 备注      |
|--------------|----------------------|---------|
| w1 lock      | FFFF_FFFF_0000_0001  |         |
| w2 lock      | FFFF_FFFE_0000_0002  |         |
| w2 wait_list | FFFF_FFFE_0000_0001  | 进入等待队列后 |
| w3 lock      | FFFF_FFFD_0000_0002  |         |
| w3 wait_list | FFFF_FFFE_0000_0001  | 进入等待队列后 |
| w1 unlock    | FFFF_FFFF_0000_0000  |         |
| w1 do_wake   | FFFF_FFFE_0000_0001  |         |
| w2 unlock    | FFFF_FFFF_0000_0000  |         |
| w2 do_wake   | FFFF_FFFF_0000_0001  |         |
| w3 unlock    | 0000_0000_0000_0000  |         |

#### 情形二

| 时刻           | count 状态            | 备注      |
|--------------|---------------------|---------|
| w1 lock      | FFFF_FFFF_0000_0001 |         |
| w2 lock      | FFFF_FFFE_0000_0002 |         |
| w3 lock      | FFFF_FFFD_0000_0003 |         |
| w2 wait_list | FFFF_FFFD_0000_0002 | 进入等待队列后 |
| w3 wait_list | FFFF_FFFE_0000_0001 | 进入等待队列后 |
| w1 unlock    | FFFF_FFFF_0000_0000 |         |
| w1 do_wake   | FFFF_FFFE_0000_0001 |         |
| w2 unlock    | FFFF_FFFF_0000_0000 |         |
| w2 do_wake   | FFFF_FFFF_0000_0001 |         |
| w3 unlock    | 0000_0000_0000_0000 |         |

#### 情形三

| 时刻           | count 状态            | 备注      |
|--------------|---------------------|---------|
| w1 lock      | FFFF_FFFF_0000_0001 |         |
| w2 lock      | FFFF_FFFE_0000_0002 |         |
| w2 wait_list | FFFF_FFFE_0000_0001 | 进入等待队列后 |
| w3 lock      | FFFF_FFFD_0000_0002 |         |
| w1 unlock    | FFFF_FFFE_0000_0001 |         |
| w3 wait_list | FFFF_FFFF_0000_0000 | 进入等待队列后 |
| w3 do_wake   | FFFF_FFFE_0000_0001 |         |
| w2 unlock    | FFFF_FFFF_0000_0000 |         |
| w2 do_wake   | FFFF_FFFF_0000_0001 |         |
| w3 unlock    | 0000_0000_0000_0000 |         |

#### 情形四

| 时刻           | count 状态            | 备注      |
|--------------|---------------------|---------|
| w1 lock      | FFFF_FFFF_0000_0001 |         |
| w2 lock      | FFFF_FFFE_0000_0002 |         |
| w3 lock      | FFFF_FFFD_0000_0003 |         |
| w1 unlock    | FFFF_FFFE_0000_0002 |         |
| w2 wait_list | FFFF_FFFE_0000_0001 | 进入等待队列后 |
| w3 wait_list | FFFF_FFFF_0000_0000 | 进入等待队列后 |
| w3 do_wake   | FFFF_FFFE_0000_0001 |         |
| w2 unlock    | FFFF_FFFF_0000_0000 |         |
| w2 do_wake   | FFFF_FFFF_0000_0001 |         |
| w3 unlock    | 0000_0000_0000_0000 |         |

#### 情形五

| 时刻             | count 状态            | 备注      |
|----------------|---------------------|---------|
| w1 lock        | FFFF_FFFF_0000_0001 |         |
| w2 lock        | FFFF_FFFE_0000_0002 |         |
| w2 wait_list   | FFFF_FFFE_0000_0001 | 进入等待队列后 |
| w1 unlock      | FFFF_FFFF_0000_0000 |         |
| w3 lock        | FFFF_FFFE_0000_0001 |         |
| w1 do_wake     | FFFF_FFFE_0000_0002 |         |
| w1 undo_write  | FFFF_FFFE_0000_0001 |         |
| w3 wait_list   | FFFF_FFFF_0000_0000 | 进入等待队列后 |
| w3 do_wake     | FFFF_FFFE_0000_0001 |         |
| w2 unlock      | FFFF_FFFF_0000_0000 |         |
| w2 do_wake     | FFFF_FFFF_0000_0001 |         |
| w3 unlock      | 0000_0000_0000_0000 |         |

#### 情形六

| 时刻             | count 状态            | 备注      |
|----------------|---------------------|---------|
| w1 lock        | FFFF_FFFF_0000_0001 |         |
| w2 lock        | FFFF_FFFE_0000_0002 |         |
| w2 wait_list   | FFFF_FFFE_0000_0001 | 进入等待队列后 |
| w1 unlock      | FFFF_FFFF_0000_0000 |         |
| w3 lock        | FFFF_FFFE_0000_0001 |         |
| w3 wait_list   | FFFF_FFFF_0000_0000 | 进入等待队列后 |
| w3 do_wake     | FFFF_FFFE_0000_0001 |         |
| w1 do_wake     | FFFF_FFFE_0000_0002 |         |
| w1 undo_write  | FFFF_FFFE_0000_0001 |         |
| w2 unlock      | FFFF_FFFF_0000_0000 |         |
| w2 do_wake     | FFFF_FFFF_0000_0001 |         |
| w3 unlock      | 0000_0000_0000_0000 |         |

#### 情形六

| 时刻             | count 状态            | 备注      |
|----------------|---------------------|---------|
| w1 lock        | FFFF_FFFF_0000_0001 |         |
| w2 lock        | FFFF_FFFE_0000_0002 |         |
| w2 wait_list   | FFFF_FFFE_0000_0001 | 进入等待队列后 |
| w1 unlock      | FFFF_FFFF_0000_0000 |         |
| w3 lock        | FFFF_FFFE_0000_0001 |         |
| w3 wait_list   | FFFF_FFFF_0000_0000 | 进入等待队列后 |
| w3 do_wake     | FFFF_FFFE_0000_0001 |         |
| w2 unlock      | FFFF_FFFF_0000_0000 |         |
| w1 do_wake     | FFFF_FFFF_0000_0001 |         |
| w3 unlock      | 0000_0000_0000_0000 |         |

## 混合类型

